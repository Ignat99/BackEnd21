package backend21.application.alert

import backend21.application.AppServiceBase
import backend21.domain.DomainException
import backend21.domain.alert.UserAlert
import backend21.domain.alert.UserAlertService
import backend21.domain.project.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserAlertAppService @Autowired constructor(private val projectService: ProjectService,
        private val userAlertService: UserAlertService):AppServiceBase(projectService) {
    fun createUserAlert(owner:String, userAlertDTO: UserAlertDTO): UserAlertDTO {
        checkOwner(userAlertDTO.projectId, owner)
        userAlertDTO.userId = owner
        val userAlert = UserAlert.fromDTO(userAlertDTO)
        return userAlertService.createUserAlert(userAlert).toDTO()
    }

    fun getAlertsByUserId(owner:String): List<UserAlertDTO> 
        = userAlertService.getAlertsByUserId(owner).map { it.toDTO() }

    fun deleteUserAlert(id: String) = userAlertService.deleteUserAlert(id)
    fun updateUserAlert(id: String, userId: String, userAlertUpdate: UserAlertDTOUpdate) {
        val userAlert = userAlertService.getById(id) ?:
            throw DomainException("User Alert with id $id doesn't exist")
        if (userAlert.hasSameUserId(userId)) {
           userAlertService.updateUserAlert(userAlert, userAlertUpdate)
        }
        else throw DomainException("User $userId isn't the owner of User Alert $id")
    }
}
