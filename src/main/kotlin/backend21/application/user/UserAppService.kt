package backend21.application.user

import backend21.application.ApplicationServiceException
import backend21.domain.DomainException
import backend21.domain.user.User
import backend21.domain.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserAppService @Autowired constructor(private val userService: UserService) {
    fun createUser(user: UserDTO): UserDTO {
        val sourceUser = User.fromDTO(user)
        try {
            userService.findByUsername(sourceUser.username!!)
            throw ApplicationServiceException("User with username ${sourceUser.username} exists")
        }
        catch(ex: DomainException) {
            val savedUser = userService.createUser(sourceUser)
            return savedUser.toDTO()
        }
    }



}
