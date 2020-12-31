package backend21.domain.alert

import backend21.domain.alert.interfaces.SystemAlertRepository
import backend21.domain.email.EmailService
import backend21.domain.project.ProjectService
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SystemExecutionAlertService @Autowired constructor(
    private val systemAlertRepository: SystemAlertRepository,
    alertLocator: AlertLocator, projectService: ProjectService,
    emailService: EmailService):ExecutionAlertServiceBase(alertLocator,
    projectService, emailService)
{
    @Scheduled(fixedDelay = 900000)
    fun process() {
        LOG.info("STARTING SystemExecutionAlertService....")
        super.process {
            systemAlertRepository.findAll()
        }
        LOG.info("END SystemExecutionAlertService....")
    }


    companion object {
//        private val LOG = LoggerFactory.getLogger(SystemExecutionAlertService::class.java)
        private val LOG = LogManager.getLogger(SystemExecutionAlertService::class.java)
    }
}
