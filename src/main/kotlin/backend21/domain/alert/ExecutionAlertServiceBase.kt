package backend21.domain.alert

import backend21.domain.email.EmailService
import backend21.domain.project.ProjectService
import backend21.domain.socialnetworks.Tweet
import backend21.domain.user.UserService


open class ExecutionAlertServiceBase(private val alertLocator: AlertLocator,
    private val projectService: ProjectService, private val emailService: EmailService)
{
    fun process(callback: (id:String) -> List<Alert>) {
        val projects = projectService.getProjects()
        projects.forEach { projectItem ->
            val alerts = callback(projectItem.id!!)
            alerts.forEach {
                val alertStrategy = alertLocator.getAlertStrategy(it.getAlertAlias(), it.getExtravars(), projectItem)
                val projectAlertExecution = alertStrategy.process()
                if (projectAlertExecution != null) {
                    if (projectAlertExecution.isAlert() && it.requiresEmail()) {
                        emailService.alert(projectAlertExecution, projectItem, "http://localhost:8080/api/")
                    }
                }
            }
        }
    }


}
