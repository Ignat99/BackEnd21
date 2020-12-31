package backend21.domain.alert.interfaces

import backend21.domain.alert.UserAlert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserAlertRepository: JpaRepository<UserAlert, String> {
    fun findById(id: String): Optional<UserAlert>
    fun findByUserId(userId: String): Optional<List<UserAlert>>
    fun findByProjectId(projectId: String): Optional<List<UserAlert>>
    fun findByProjectIdAndActive(projectId: String, active:Int): Optional<List<UserAlert>>
}
