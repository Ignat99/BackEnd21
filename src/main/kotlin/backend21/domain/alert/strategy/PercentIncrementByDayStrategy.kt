package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import java.text.SimpleDateFormat
import java.util.*

const val HOUR_OF_DAY = 23
const val MINUTE = 59
const val SECOND = 59
const val ZERO = 0

class PercentIncrementByDayStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val project: Project, private val ratio: Float,
    private val date: String? = null) : AlertStrategy 
{
    override fun getTweets(from: String,
        to: String, tweetId: String?,
        threatLevel: Int?): TweetResults = elasticTweetsService.getTweetsFromProject(
             project, from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.PERCENT_ALIAS

    override fun process(): ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
           .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
            0, AlertConstants.PERCENT_ALIAS, project.id!!, "ratio=$ratio")
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val endDate = date ?: simpleDateFormat.format(Date())
        val minus = 0
        val (endTime, startTime) = generateRangeDates(simpleDateFormat, endDate, minus)
        val startTimeStr = simpleDateFormat.format(startTime)
        val endTimeStr = simpleDateFormat.format(endTime)
        val tweetResults = elasticTweetsService.getTweetsFromProject(project, startTimeStr,
             endTimeStr, limit = 1)
        var showAlert = 0
        if (lastExecution.isPresent) {
            val (endTimeYesterday, startTimeYesterday) = generateRangeDates(
              simpleDateFormat, endDate, -1)
            val tweetResultsYesterday = elasticTweetsService.getTweetsFromProject(project,
              simpleDateFormat.format(startTimeYesterday),
               simpleDateFormat.format(endTimeYesterday), limit = 1)
            if (((tweetResults.total - tweetResultsYesterday.total).toFloat() / 
                tweetResultsYesterday.total.toFloat()) >= ratio) showAlert = 1
        }
        return projectAlertExecutionRepository.save(ProjectAlertExecution(null, project.id!!,
            AlertConstants.PERCENT_ALIAS, simpleDateFormat.parse(startTimeStr),
            simpleDateFormat.parse(endTimeStr), "ratio=$ratio,num=${tweetResults.total}",
            showAlert, simpleDateFormat.parse(endDate)))
    }

    private fun generateRangeDates(simpleDateFormat: SimpleDateFormat,
        endDate: String, minus: Int): Pair<Long, Long>
    {
        val cal = Calendar.getInstance()
        val now = simpleDateFormat.parse(endDate)
        cal.time = now
        cal.add(Calendar.DAY_OF_YEAR, minus)
        cal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY)
        cal.set(Calendar.MINUTE, MINUTE)
        cal.set(Calendar.SECOND, SECOND)
        val endTime = cal.timeInMillis
        cal.time = now
        cal.add(Calendar.DAY_OF_YEAR, minus)
        cal.set(Calendar.HOUR_OF_DAY, ZERO)
        cal.set(Calendar.MINUTE, ZERO)
        cal.set(Calendar.SECOND, ZERO)
        val startTime = cal.timeInMillis
        return Pair(endTime, startTime)
    }

}
