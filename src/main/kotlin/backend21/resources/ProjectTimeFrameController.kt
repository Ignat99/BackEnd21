package backend21.resources

import backend21.application.project.ProjectAppService
import backend21.application.user.UserAppService
import backend21.application.user.ProjectTimeFrameDTO
import backend21.domain.DomainException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ProjectTimeFrameController: BaseController() {
    @Autowired
    private lateinit var projectAppService: ProjectAppService

    @RequestMapping(path = ["/timeframe"], method = [(RequestMethod.POST)])
    fun createProjectTimeframe(@RequestBody projectTimeFrame: ProjectTimeFrameDTO): ResponseEntity<String> {
        checkCredentials(projectTimeFrame.toString()) {
            ResponseEntity.ok(projectAppService.createProjectTimeFrame(projectTimeFrame))
        }
        return return200()
    }

    @GetMapping("/timeframe/{id}")
    fun getProjectTimeFrame(@PathVariable("id") id: String): ResponseEntity<ProjectTimeFrameDTO> {
        return ResponseEntity.ok(checkCredentials("id=$id") {
            try {
                ResponseEntity.ok(projectAppService.getTimeFrameByOwnerAndId(it, id))
            }
            catch(e: DomainException) {
                ResponseEntity.notFound().build()
            }
        }) as ResponseEntity<ProjectTimeFrameDTO>
    }
}


