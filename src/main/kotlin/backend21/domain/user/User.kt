package backend21.domain.user


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import backend21.application.user.UserDTO
import org.springframework.security.crypto.bcrypt.BCrypt
import javax.persistence.Entity
import javax.persistence.Id
@Entity
class User(@Id var id: String? = null, @JsonDeserialize val username: String?=null,
           val email: String? = null,
           val password: String? = null, val name: String?=null, val surname: String?=null) {

    companion object {
        val SALT = "\$2a\$10\$cNb7QW4aaL473DMRl6RjTe"
        fun fromDTO(source: UserDTO): User {
            return User(null, source.username, source.email, BCrypt.hashpw(source.password, SALT),
                source.name, source.surname)
        }
    }

    fun hasSamePassword(password: String): Boolean = this.password.equals(BCrypt.hashpw(password,
        SALT))


    fun toDTO(): UserDTO = UserDTO(this.username!!, this.email!!, "", this.name!!, this.surname!!)

}
