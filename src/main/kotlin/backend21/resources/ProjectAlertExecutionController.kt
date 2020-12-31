package backend21.resources

import backend21.application.alert.*
import backend21.application.project.TweetResultsDTO
import backend21.domain.alert.ListProjectAlertExecutions
import backend21.domain.alert.ProjectAlertExecution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat

@RestController
@RequestMapping("/api")
class ProjectAlertExecutionController @Autowired constructor(private val projectAlertExecutionAppService: ProjectAlertExecutionAppService): BaseController() {

    @PostMapping("/projectexecutionalerts")
    fun createProjectExecutionAlerts():ResponseEntity<String> {
        return ResponseEntity.ok(checkCredentials("") {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val pae1 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
            val pae2 = ProjectAlertExecution(null, "22", "hashtag_in", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
            val pae3 = ProjectAlertExecution(null, "23", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-23T12:12:12"), 1)
            val pae4 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000,ratio=0.1", 1, simpleDateFormat.parse("2018-04-24T12:12:12"))
            val pae5 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=300000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
            val pae6 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), 1)
            val pae7 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
            val pae8 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), 1)
            val pae9 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
            ResponseEntity.ok(projectAlertExecutionAppService.create(listOf(pae1, pae2, pae3, pae4, pae5, pae6, pae7, pae8, pae9)))
        }) as ResponseEntity<String>


    }


    @GetMapping("/projectexecutionalerts")
    fun getShowAlerts(@RequestBody alertFilter: ProjectExecutionAlertCommandDTO): ResponseEntity<ListProjectAlertExecutionsDTO> {
        return ResponseEntity.ok(checkCredentials(alertFilter.toString()) {
            ResponseEntity.ok(projectAlertExecutionAppService.getShowAlerts(alertFilter.projectId, alertFilter.readId, alertFilter.unreadId).toDTO())
        }) as ResponseEntity<ListProjectAlertExecutionsDTO>
    }

    @GetMapping("/tweetsalerts/{id}")
    fun getTweetsForAlerts(@PathVariable("id") id: String, @RequestBody paginationProjectAlertExecutionCommandDTO: PaginationProjectAlertExecutionCommandDTO): ResponseEntity<TweetResultsDTO> {
        return ResponseEntity.ok(checkCredentials("alertId=$id") {
            ResponseEntity.ok(projectAlertExecutionAppService.getTweetsForAlert(id, paginationProjectAlertExecutionCommandDTO.tweetId, paginationProjectAlertExecutionCommandDTO.threatLevel).toDTO())
        }) as ResponseEntity<TweetResultsDTO>
    }

    @DeleteMapping("/projectexecutionalerts")
    fun deleteProjectExecutionAlerts(@RequestBody ids: ProjectExecutionAlertListDTO):ResponseEntity<String>  {
        checkCredentials(ids.toString()) {
            ResponseEntity.ok(projectAlertExecutionAppService.deleteProjectExecutionAlerts(ids.ids))
        }
        return return201()
    }

    @PostMapping("/archiveprojectexecutionalerts")
    fun archiveProjectExecutionAlerts(@RequestBody ids: ProjectExecutionAlertListDTO):ResponseEntity<String>  {
        checkCredentials(ids.toString()) {
            ResponseEntity.ok(projectAlertExecutionAppService.archiveProjectExecutionAlerts(ids.ids))
        }
        return return201()
    }
}