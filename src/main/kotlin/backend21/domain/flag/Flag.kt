package backend21.domain.flag

import backend21.application.flag.FlagDTO
import backend21.application.user.UserDTO
import backend21.domain.user.User
import org.springframework.security.crypto.bcrypt.BCrypt
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Flag(@Id var id: String? = null, val name: String?=null, val projectId: String?=null,
    val active: Int =1)
{
    val lowerName: String? = name?.toLowerCase()

    companion object {

        fun fromDTO(source: FlagDTO): Flag = Flag(null, source.name, source.projectId,
            source.active)

    }



    fun toDTO(): FlagDTO = FlagDTO(this.id, this.name, this.projectId, this.active)

}
