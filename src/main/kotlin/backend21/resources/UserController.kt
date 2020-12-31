package backend21.resources

import backend21.application.login.*
import backend21.application.user.UserAppService
import backend21.application.user.UserDTO
import backend21.domain.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController: BaseController() {
    @Autowired
    private lateinit var userAppService: UserAppService

    @Autowired
    private lateinit var loginAppService: LoginAppService

    @RequestMapping(path = ["/user"], method = [(RequestMethod.POST)])
    fun registerUser(@RequestBody user: UserDTO): ResponseEntity<String> {
        userAppService.createUser(user)
        return return201()
    }




    @RequestMapping(path = ["/login"], method = [(RequestMethod.POST)])
    fun login(@RequestBody login: LoginDTO): ResponseEntity<CredentialsDTO> = ResponseEntity.ok(loginAppService.login(login))

    @RequestMapping(path = ["/refreshtoken"], method = [(RequestMethod.POST)])
    fun refreshToken(@RequestBody refreshToken: TokenDTO): ResponseEntity<OAuthCredentialsDTO> = ResponseEntity.ok(loginAppService.refreshToken(refreshToken))
}