package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
//import sun.java2d.pipe.SpanShapeRenderer
import java.text.SimpleDateFormat
import java.util.*

class PostThroughputByHourAlertStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val project: Project,
    private val numPosts: Long, private val date: String? = null) : AlertStrategy 
{
    override fun getTweets(from: String, to: String, tweetId: String?,
        threatLevel: Int?): TweetResults = elasticTweetsService.getTweetsFromProject(project,
            from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.HOUR_ALIAS

    override fun process(): ProjectAlertExecution? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val endDate = date ?: simpleDateFormat.format(Date())
        val (endTime, startTime) = generateRangeDates(simpleDateFormat, endDate)
        val startTimeStr = simpleDateFormat.format(startTime)
        val endTimeStr = simpleDateFormat.format(endTime)
        var showAlert = 0
        val totalResults = elasticTweetsService.getTweetsFromProject(project, startTimeStr,
            endTimeStr)
        val achievesThreshold = totalResults.total > numPosts
        if (achievesThreshold) showAlert = 1
        return projectAlertExecutionRepository.save(ProjectAlertExecution(null, project.id!!,
            AlertConstants.HOUR_ALIAS, simpleDateFormat.parse(startTimeStr),
            simpleDateFormat.parse(endTimeStr), "num=$numPosts", showAlert,
            simpleDateFormat.parse(endDate)))
    }

    private fun generateRangeDates(simpleDateFormat: SimpleDateFormat, endDate: String): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val now = simpleDateFormat.parse(endDate)
        cal.time = now
        val endTime = cal.timeInMillis
        cal.time = now
        cal.add(Calendar.HOUR_OF_DAY, -1)
        val startTime = cal.timeInMillis
        return Pair(endTime, startTime)
    }

}
