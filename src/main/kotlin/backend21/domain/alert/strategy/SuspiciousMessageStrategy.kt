package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import java.text.SimpleDateFormat
import java.util.*

class SuspiciousMessageStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val project: Project, private val threatLevelMin: Int?=null,
    private val date:String?=null): AlertStrategy 
{
    override fun getTweets(from: String, to: String, tweetId: String?,
        threatLevel: Int?): TweetResults = elasticTweetsService.getTweetsFromProject(
            project, from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.SUSPICIOUS_ALIAS

    override fun process(): ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
            .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
                 0,AlertConstants.SUSPICIOUS_ALIAS, project.id!!,
                 "threat_level_min=$threatLevelMin")
        var startDate = project.createdAt
        if (lastExecution.isPresent) startDate = lastExecution.get().first().executedAt
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val endDate = date ?: simpleDateFormat.format(Date())
        val startDateStr = simpleDateFormat.format(startDate)
        val tweetsResult = elasticTweetsService.getTweetsFromProject(project, startDateStr,
            endDate, null, threatLevelMin, 1)
        var showAlert = 0
        if (tweetsResult.total > 0) showAlert = 1
        return projectAlertExecutionRepository.save(ProjectAlertExecution(null, project.id!!,
            AlertConstants.SUSPICIOUS_ALIAS, startDate, simpleDateFormat.parse(endDate),
            "threat_level_min=$threatLevelMin", showAlert, simpleDateFormat.parse(endDate)))
    }
}
