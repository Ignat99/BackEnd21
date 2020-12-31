package backend21.resources

import backend21.application.alert.UserAlertAppService
import backend21.application.alert.UserAlertDTO
import backend21.application.alert.UserAlertDTOUpdate
import backend21.application.project.ProjectDTOUpdate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserAlertController @Autowired constructor(private val userAlertAppService: UserAlertAppService):BaseController() {
    @RequestMapping(path = ["/useralert"], method = [(RequestMethod.POST)])
    fun createUserAlert(@RequestBody userAlert: UserAlertDTO): ResponseEntity<String> {
        checkCredentials(userAlert.toString()) {
            ResponseEntity.ok(userAlertAppService.createUserAlert(it, userAlert))
        }
        return return201()
    }

    @GetMapping("/useralerts")
    fun getAlertsByUser(): ResponseEntity<List<UserAlertDTO>> {
        return ResponseEntity.ok(checkCredentials("") {
            ResponseEntity.ok(userAlertAppService.getAlertsByUserId(it))
        }) as ResponseEntity<List<UserAlertDTO>>
    }

    @DeleteMapping("/useralerts/{id}")
    fun deleteUserAlert(@PathVariable("id") id: String):ResponseEntity<String> {
        checkCredentials("alertId=$id") {
            ResponseEntity.ok(userAlertAppService.deleteUserAlert(id))
        }
        return return201()
    }


    @RequestMapping(path =["/useralerts/{id}"], method = [RequestMethod.POST])
    fun updateUserAlert(@PathVariable("id") id: String, @RequestBody userAlert: UserAlertDTOUpdate): ResponseEntity<String> {
        checkCredentials("alertId=$id, $userAlert") {
            ResponseEntity.ok(userAlertAppService.updateUserAlert(id, it, userAlert))
        }
        return return200()
    }
}