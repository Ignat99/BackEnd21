package backend21.resources


import backend21.application.project.KeywordsDTO
import backend21.application.project.ProjectAppService
import backend21.application.project.ProjectDTO
import backend21.application.project.ProjectDTOUpdate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ProjectController:BaseController() {
    @Autowired
    private lateinit var projectAppService: ProjectAppService

    @RequestMapping(path = ["/project"], method = [(RequestMethod.POST)], produces = ["application/json;charset=UTF-8"])
    fun createProject(@RequestBody project: ProjectDTO) : ResponseEntity<ProjectDTO> {
        return ResponseEntity.ok(checkCredentials("") {
            project.owner = it
            ResponseEntity.ok(projectAppService.createProject(project))
        }) as ResponseEntity<ProjectDTO>

    }

    @GetMapping("/keywords")
    fun keywords(): ResponseEntity<KeywordsDTO> = ResponseEntity.ok(KeywordsDTO(projectAppService.getAllKeywords(), listOf(), listOf()))



    @GetMapping("/project")
    fun getProjects(term:String?=null): ResponseEntity<List<ProjectDTO>> {
        return ResponseEntity.ok(checkCredentials("term=${term?:""}") {
            ResponseEntity.ok(projectAppService.getProjectsByOwner(it, term))
        }) as ResponseEntity<List<ProjectDTO>>
    }

    @GetMapping("/project/{id}")
    fun getProject(@PathVariable("id") id: String): ResponseEntity<ProjectDTO> {
        return ResponseEntity.ok(checkCredentials("projectId=$id") {
            ResponseEntity.ok(projectAppService.getProjectByOwnerAndId(id, it))
        }) as ResponseEntity<ProjectDTO>
    }

//    @GetMapping("/project/{id}")
//    fun getProjectArchived(@PathVariable("id") id: String): ResponseEntity<ProjectDTO> {
//        return ResponseEntity.ok(checkCredentials("projectId=$id") {
//            ResponseEntity.ok(projectAppService.getProjectByOwnerAndIdArchived(id, it))
//        }) as ResponseEntity<ProjectDTO>
//    }

    @RequestMapping(path =["/project/{id}"], method = [RequestMethod.DELETE])
    fun deleteProject(@PathVariable("id") id: String): ResponseEntity<String> {
        checkCredentials("projectId=$id") {
            ResponseEntity.ok(projectAppService.deleteProject(id, it))
        }
        return return204()
    }



    @RequestMapping(path =["/projects"], method = [RequestMethod.DELETE])
    fun deleteProjects(): ResponseEntity<String> {
        checkCredentials("projectId=$id") {
            ResponseEntity.ok(projectAppService.deleteAllProjects())
        }
        return return204()
    }


    @RequestMapping(path =["/project/{id}"], method = [RequestMethod.POST])
    fun updateProject(@PathVariable("id") id: String, @RequestBody project: ProjectDTOUpdate): ResponseEntity<String> {
        checkCredentials("projectId=$id") {
            ResponseEntity.ok(projectAppService.updateProject(id, it, project))
        }
        return return200()
    }


}
