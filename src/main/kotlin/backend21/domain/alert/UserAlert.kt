package backend21.domain.alert

import backend21.application.alert.UserAlertDTO
import backend21.domain.DomainException
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import kotlin.collections.HashMap

@Entity
class UserAlert(@Id var id: String? = null, val projectId: String?=null, val userId: String?=null,
    val alias: String?=null, extravars:HashMap<String, String>?=null,  val createdAt: Date?=null,
    val type:String?="user", val active:Int?=1, val email:Int?=1):Alert 
{
    override fun requiresEmail(): Boolean = this.email == 1

    val extravars: String? = initExtravars(extravars)
    init {
        val allowedTypes = arrayListOf("user", "content", "location", "threat")
        if (!allowedTypes.contains(this.type)) 
            throw DomainException("Invalid alert type ${this.type}")
    }

    override fun getAlertAlias(): String = this.alias!!

    fun initExtravars(extravars: HashMap<String, String>?): String? {
        return if (extravars == null) null
        else {
            val extravarsList: ArrayList<String> = extravars.keys.map {
                "$it=${extravars[it]}"
            } as ArrayList<String>
            extravarsList.joinToString(",")
        }
    }

    override fun getExtravars(): HashMap<String, String> {
        val toManage = this.extravars
        return UserAlert.toHashMap(toManage)
    }


    fun toDTO(): UserAlertDTO {
        val userAlertDTO = UserAlertDTO(this.id, this.projectId!!, this.alias!!, this.extravars,
            this.createdAt, this.type!!, this.active, this.email)
        userAlertDTO.userId = this.userId
        return userAlertDTO
    }

    fun hasSameUserId(userId: String): Boolean = this.userId == userId


    companion object {
        fun toHashMap(toManage: String?): HashMap<String, String> {
            val extravarsHashMap: HashMap<String, String> = hashMapOf()
            toManage?.split(",")?.map {
                val split = it.split("=")
                if (split.size == 2) extravarsHashMap[split[0]] = split[1]
            }
            return extravarsHashMap
        }
        fun fromDTO(userAlertDTO: UserAlertDTO): UserAlert = UserAlert(null,
            userAlertDTO.projectId, userAlertDTO.userId, userAlertDTO.alias,
            toHashMap(userAlertDTO.extravars), userAlertDTO.createdAt,
            userAlertDTO.type, userAlertDTO.active, userAlertDTO.email)
    }
}
