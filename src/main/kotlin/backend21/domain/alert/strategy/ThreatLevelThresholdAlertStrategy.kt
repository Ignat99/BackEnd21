package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import java.text.SimpleDateFormat
import java.util.*


class ThreatLevelThresholdAlertStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val threatLevel: Long,
    private val project: Project, private val date: String? = null) : AlertStrategy 
{
    override fun getTweets(from: String, to: String, tweetId: String?,
        threatLevel: Int?): TweetResults = elasticTweetsService
        .getTweetsFromProject(project, from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.THRESHOLD_ALIAS

    override fun process():ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
            .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
                0,AlertConstants.THRESHOLD_ALIAS, project.id!!,
                "threat_level=${this.threatLevel}")
        if (!lastExecution.isPresent) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val endDate = date ?: simpleDateFormat.format(Date())
            val totalThreadScores = elasticTweetsService.getTotalThreadScoresByProject(project)
            if (totalThreadScores.total >= threatLevel)
                return projectAlertExecutionRepository.save(ProjectAlertExecution(null,
                    project.id, AlertConstants.THRESHOLD_ALIAS, 
                    project.createdAt, simpleDateFormat.parse(endDate),
                    "threat_level=${this.threatLevel}", 1, simpleDateFormat.parse(endDate)))
        }
        return null
    }

}
