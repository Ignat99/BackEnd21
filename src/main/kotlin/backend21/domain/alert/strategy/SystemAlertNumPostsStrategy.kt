package backend21.domain.alert.strategy

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.socialnetworks.ElasticTweetsService
import org.springframework.beans.factory.annotation.Autowired
import backend21.domain.project.Project
import backend21.domain.socialnetworks.TweetResults
import java.text.SimpleDateFormat
import java.util.*

open class SystemAlertNumPostsStrategy @Autowired constructor(
    private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    private val numPosts: Long, private val project: Project,
    private val date: String? = null) : AlertStrategy
{
    override fun getTweets(from: String, to: String, tweetId: String?,
        threatLevel: Int?): TweetResults = elasticTweetsService.getTweetsFromProject(
            project, from, to, tweetId, threatLevel)

    override fun getName(): String = AlertConstants.SYSTEM_ALIAS

    override fun process(): ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
            .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
            0,AlertConstants.SYSTEM_ALIAS, project.id!!, "num=${this.numPosts}")
        if (!lastExecution.isPresent) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val endDate = date ?: simpleDateFormat.format(Date())
            val startDateStr = simpleDateFormat.format(project.createdAt)
            val tweetsFromProject = elasticTweetsService.getTweetsFromProject(
                project, startDateStr, endDate)
            if (tweetsFromProject.total >= this.numPosts) {
                return projectAlertExecutionRepository.save(ProjectAlertExecution(
                    null, project.id, AlertConstants.SYSTEM_ALIAS,
                    simpleDateFormat.parse(startDateStr), simpleDateFormat.parse(endDate),
                    "num=${this.numPosts}", 1, simpleDateFormat.parse(endDate)))
            }
        }
        return null
    }

}
