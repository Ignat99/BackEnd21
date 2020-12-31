package backend21.domain.user

import backend21.application.user.EmailValidator
import backend21.domain.DomainException
import backend21.domain.user.interfaces.UserRepository
import org.hibernate.id.GUIDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserService @Autowired constructor(private val userRepository: UserRepository)
{
    fun createUser(user: User): User {
        user.id = UUID.randomUUID().toString()
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User {
        val user = userRepository.findByUsername(username)
        if (user.isPresent) return user.get()
        throw DomainException("User with username $username doesn't exist")
    }

    fun findById(id: String): User = userRepository.findById(id)
        .orElseThrow { DomainException("User with id $id doesn't exist") }
}
