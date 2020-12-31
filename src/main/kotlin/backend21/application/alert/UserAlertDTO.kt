package backend21.application.alert

import java.util.*

data class UserAlertDTO(
        var id: String? = null,
        val projectId: String,
        val alias: String,
        val extravars:String?=null,
        val createdAt: Date?=Date(),
        val type:String,
        val active:Int?=1,
        val email:Int?=1)
    {
        var userId: String?=null
    }
