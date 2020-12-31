package backend21.domain.alert

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class SystemAlert(@Id  var id: String? = null,  val alias: String? = null,
    extravars: HashMap<String, String>? = null,  val createdAt: Date? = null):Alert
{
    override fun requiresEmail(): Boolean = false

    override fun getAlertAlias(): String = this.alias!!

    val extravars: String? = initExtravars(extravars)

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



    fun toHashMap(toManage: String?): HashMap<String, String> {
        val extravarsHashMap: HashMap<String, String> = hashMapOf()
        toManage?.split(",")?.map {
            val split = it.split("=")
            extravarsHashMap[split[0]] = split[1]
        }
        return extravarsHashMap
    }
}
