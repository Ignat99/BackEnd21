package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import java.text.SimpleDateFormat
import java.util.*

open class ThreatLevelChangeStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val project: Project, private val date: String? = null) : AlertStrategy 
{
    override fun getTweets(from: String, to: String,
        tweetId: String?, threatLevel: Int?): TweetResults = elasticTweetsService
        .getTweetsFromProject(project, from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.THREAT_ALIAS


    override fun process(): ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
            .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
            0,AlertConstants.THREAT_ALIAS, project.id!!, "")

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val endDate = date ?: simpleDateFormat.format(Date())
        val totalThreadScores = elasticTweetsService.getTotalThreadScoresByProject(project)
        var showAlert = 0
        val projectThreatLevel = totalThreadScores.getThreatLevel()
        var extravars = "threat_level=$projectThreatLevel"
        if (lastExecution.isPresent) {
            val lastThreatAlert = lastExecution.get().first()
            val threatLevel = lastThreatAlert.extravars?.split(",")?.first()?.split("=")?.last()?.toInt()
            if (threatLevel != projectThreatLevel) {
                showAlert = 1
                extravars += ",old=$threatLevel"
            }
        }
        return projectAlertExecutionRepository.save(ProjectAlertExecution(null,
            project.id, AlertConstants.THREAT_ALIAS, project.createdAt,
            simpleDateFormat.parse(endDate), extravars, showAlert,
            simpleDateFormat.parse(endDate)))
    }
}
