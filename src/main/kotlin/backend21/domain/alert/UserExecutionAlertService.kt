package backend21.domain.alert

import backend21.domain.email.EmailService
import backend21.domain.project.ProjectService
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class UserExecutionAlertService @Autowired constructor(
    private val userAlertService: UserAlertService,
    alertLocator: AlertLocator,projectService: ProjectService,
    emailService: EmailService):ExecutionAlertServiceBase(alertLocator,
    projectService, emailService)
{
    @Scheduled(fixedDelay = 900000)
    fun process() {
        LOG.info("STARTING UserExecutionAlertService....")
        super.process {
            userAlertService.getActiveAlertsByProject(it)
        }
        LOG.info("END UserExecutionAlertService...")
    }

    companion object {
//        private val LOG = LoggerFactory.getLogger(UserExecutionAlertService::class.java)
        private val LOG = LogManager.getLogger(UserExecutionAlertService::class.java)
    }
}
