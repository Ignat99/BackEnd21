package backend21.domain.alert

import backend21.domain.DomainException
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.ProjectService
import backend21.domain.socialnetworks.Tweet
import backend21.domain.socialnetworks.TweetResults
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Service
class ProjectAlertExecutionService @Autowired constructor(private val alertLocator: AlertLocator,
    private val projectService: ProjectService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository)
{
    fun getShowAlerts(projectId: String? = null, readId: Int? = null,
        unreadId: Int? = null): ListProjectAlertExecutions
    {
        val unread: Optional<List<ProjectAlertExecution>>
        val read: Optional<List<ProjectAlertExecution>>
        if (projectId != null) {
            read = if (readId != null) 
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(
                    0,projectId, 1, 1, readId)
            else
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(
                    0,projectId, 1, 1)
            unread = if (unreadId != null)
              projectAlertExecutionRepository
                .findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(
                 0,projectId, 1, 0, unreadId)
            else
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(
                   0,projectId, 1, 0)
        }
        else {
            read = if (readId != null)
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(
                   0,1, 1, readId)
            else
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(
                   0,1, 1)
            unread = if (unreadId != null)
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(
                    0,1, 0, unreadId)
            else
                 projectAlertExecutionRepository
                  .findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 0)
        }
        var unreadResult = arrayListOf<ProjectAlertExecution>()
        if (unread.isPresent) unreadResult = unread.get() as ArrayList<ProjectAlertExecution>
        var readResult = arrayListOf<ProjectAlertExecution>()
        if (read.isPresent) readResult = read.get() as ArrayList<ProjectAlertExecution>
        return ListProjectAlertExecutions(unreadResult, readResult)
    }


    fun getTweetsForAlert(alertId:String, tweetId: String?=null,
        threatLevel:Int?=null): TweetResults
    {
        val retrievedAlert = projectAlertExecutionRepository.findByArchivedAndId(0,alertId)
        if (retrievedAlert.isPresent) {
            val alert = retrievedAlert.get()
            if (alert.isAlert()) {
                val project = projectService.getById(alert.projectId!!)
                val strategy = alertLocator.getAlertStrategy(alert.alias!!,
                    UserAlert.toHashMap(alert.extravars), project)
                return strategy.getTweets(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .format(alert.fromDate), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                      .format(alert.toDate), tweetId, threatLevel)
            }
            else throw DomainException("Alert with id $alertId doesn't exist")
        }
        else throw DomainException("Alert with id $alertId doesn't exist")
    }

    fun deleteProjectExecutionAlerts(ids: List<String>) {
        ids.forEach {
            try {
                projectAlertExecutionRepository.delete(it)
            }
            catch(e: EmptyResultDataAccessException) {
                println(e)
            }
        }
    }

    fun archiveProjectExecutionAlerts(ids: List<String>) {
        ids.forEach {
            val projectAlertExecution = projectAlertExecutionRepository.findByArchivedAndId(0, it)
            if (projectAlertExecution.isPresent) {
                val projectItem = projectAlertExecution.get()
                val updatedProjectItem = ProjectAlertExecution(projectItem.id,
                    projectItem.projectId, projectItem.alias, projectItem.fromDate,
                    projectItem.toDate, projectItem.extravars, projectItem.showAlert,
                    projectItem.executedAt, projectItem.visited, 1)
                projectAlertExecutionRepository.save(updatedProjectItem)
            }
        }

    }

    fun create(paes: List<ProjectAlertExecution>) {
        projectAlertExecutionRepository.deleteAll()
        projectAlertExecutionRepository.save(paes)
    }
}
