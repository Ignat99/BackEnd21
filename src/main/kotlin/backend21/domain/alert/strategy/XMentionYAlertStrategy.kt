package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import java.text.SimpleDateFormat
import java.util.*

open class XMentionYAlertStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val project: Project, private val screenName1: String,
    private val screenName2: String, private val date: String? = null) : AlertStrategy 
{
    override fun getTweets(from: String, to: String, tweetId: String?,
        threatLevel: Int?): TweetResults = elasticTweetsService.getTweetsFromProjectAndXMentionsY(
            screenName1, screenName2, project, from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.XY_ALIAS

    override fun process():ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
            .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
            0,AlertConstants.XY_ALIAS, project.id!!,
            "screenname1=$screenName1,screenname2=$screenName2")
        var startDate = project.createdAt
        if (lastExecution.isPresent) startDate = lastExecution.get().first().executedAt
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val endDate = date ?: simpleDateFormat.format(Date())
        val startDateStr = simpleDateFormat.format(startDate)
        val tweetsFromProject = elasticTweetsService.getTweetsFromProjectAndXMentionsY(
            screenName1, screenName2, project, startDateStr, endDate)
        var showAlert = 0
        if (tweetsFromProject.total > 0) showAlert = 1
        return projectAlertExecutionRepository.save(ProjectAlertExecution(
            null, project.id, AlertConstants.XY_ALIAS, simpleDateFormat.parse(startDateStr),
                simpleDateFormat.parse(endDate),
                "screenname1=$screenName1,screenname2=$screenName2", showAlert,
                simpleDateFormat.parse(endDate)))
    }

}
