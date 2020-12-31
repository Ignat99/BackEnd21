package backend21.domain.alert

import backend21.application.alert.UserAlertDTOUpdate
import backend21.domain.alert.interfaces.UserAlertRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAlertService @Autowired constructor(private val userAlertRepository: UserAlertRepository) {
    fun createUserAlert(userAlert: UserAlert):UserAlert {
        userAlert.id = UUID.randomUUID().toString()
        return userAlertRepository.save(userAlert)
    }

    fun getAlertsByUserId(userId: String): List<UserAlert> = userAlertRepository.findByUserId(
         userId).orElse(emptyList())

    fun getActiveAlertsByProject(projectId: String):List<UserAlert> {
        val userAlerts = userAlertRepository.findByProjectIdAndActive(projectId, 1)
        if (!userAlerts.isPresent) return emptyList()
        return userAlerts.get()
    }

    fun deleteUserAlert(id: String) {
        try {
            userAlertRepository.delete(id)
        }
        catch(ex: EmptyResultDataAccessException) {}
    }

    fun getById(id: String): UserAlert? = userAlertRepository.findById(id).orElse(null)
    fun updateUserAlert(userAlert: UserAlert, userAlertUpdate: UserAlertDTOUpdate) {
        val updatedUserAlert = UserAlert(userAlert.id, userAlert.projectId, userAlert.userId,
            userAlert.alias, userAlert.getExtravars(), userAlert.createdAt, userAlert.type,
            userAlertUpdate.active?:userAlert.active, userAlertUpdate.email?:userAlert.email)
        userAlertRepository.save(updatedUserAlert)
    }
}
