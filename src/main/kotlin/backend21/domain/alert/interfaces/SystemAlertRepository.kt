package backend21.domain.alert.interfaces

import backend21.domain.alert.SystemAlert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SystemAlertRepository: JpaRepository<SystemAlert, String> {
    fun findById(id: String): Optional<SystemAlert>
}
