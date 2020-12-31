package backend21.application.alert

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.ProjectAlertExecutionService
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProjectAlertExecutionAppService @Autowired 
        constructor(private val projectAlertExecutionService: ProjectAlertExecutionService) {
    fun getShowAlerts(projectId: String?=null, readId: Int?=null, unreadId:Int?=null) 
        = projectAlertExecutionService.getShowAlerts(projectId, readId, unreadId)
    fun getTweetsForAlert(alertId: String, tweetId: String?=null, threatLevel:Int?=null): TweetResults 
        = projectAlertExecutionService.getTweetsForAlert(alertId, tweetId, threatLevel)
    fun deleteProjectExecutionAlerts(ids: List<String>) 
        = projectAlertExecutionService.deleteProjectExecutionAlerts(ids)
    fun archiveProjectExecutionAlerts(ids: List<String>) 
        = projectAlertExecutionService.archiveProjectExecutionAlerts(ids)
    fun create(paes: List<ProjectAlertExecution>) = projectAlertExecutionService.create(paes)

}
