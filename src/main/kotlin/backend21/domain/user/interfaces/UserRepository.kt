package backend21.domain.user.interfaces

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import backend21.domain.user.User
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, String> {
//    fun createUser(user: User): Optional<User>
    fun findByUsername(username: String): Optional<User>
    fun findById(id: String): Optional<User>
}
