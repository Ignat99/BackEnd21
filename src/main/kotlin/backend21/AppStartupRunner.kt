package backend21

import backend21.application.project.ProjectAppService
import backend21.application.project.ProjectDTO
import backend21.domain.project.ProjectService
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component

@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
@EnableScheduling
@Component
class AppStartupRunner : ApplicationRunner {

    @Autowired
    private lateinit var projectAppService: ProjectAppService


    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        LOG.info("Your application started with option names : {}", args.optionNames)

        val project = ProjectDTO("1", "test", "keyword", listOf("haraam"), 
            listOf(), listOf("twitter"), listOf(), "test project")
        project.owner = "666"
        projectAppService.createProject(project)
    }

    companion object {
//        private val LOG = LoggerFactory.getLogger(AppStartupRunner::class.java)
        private val LOG = LogManager.getLogger(AppStartupRunner::class.java)
    }

}
