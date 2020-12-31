package backend21.domain.alert.strategy

import backend21.domain.alert.Alert
import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import java.text.SimpleDateFormat
import java.util.*

abstract class FeatureInFieldStrategyBase @Autowired constructor(
    protected open val elasticTweetsService: ElasticTweetsService,
    protected open val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    protected open val project: Project, protected open val feature: String,
    protected open val field:String, protected open val alias:String,
    protected open val varInfo:String, protected open val date:String?=null): AlertStrategy
{
    override fun process():ProjectAlertExecution? {
        val lastExecution = projectAlertExecutionRepository
         .findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
           0, alias, project.id!!, "$varInfo=${this.feature}")
        var startDate = project.createdAt
        if (lastExecution.isPresent) startDate = lastExecution.get().first().executedAt
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val endDate = date?: simpleDateFormat.format(Date())
        val startDateStr = simpleDateFormat.format(startDate)
        val tweetsFromProject = elasticTweetsService.getTweetsFromProjectWithFeatureInField(
         field, feature, project, startDateStr, endDate)
        var showAlert = 0
        if (tweetsFromProject.total > 0) showAlert = 1
        return projectAlertExecutionRepository.save(ProjectAlertExecution(
         null, project.id, alias, startDate, simpleDateFormat.parse(endDate), 
          "$varInfo=${this.feature}", showAlert, simpleDateFormat.parse(endDate)))
    }


    override fun getTweets(from:String, to:String, tweetId: String?,
      threatLevel: Int?): TweetResults 
    {
        return elasticTweetsService.getTweetsFromProjectWithFeatureInField(field, feature,
           project, from, to)
    }

}
