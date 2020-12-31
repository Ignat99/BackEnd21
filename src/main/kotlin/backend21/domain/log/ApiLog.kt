package backend21.domain.log

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
data class ApiLog(@Id @GeneratedValue(strategy= GenerationType.IDENTITY) val id: String? = null,
     val userId: String?=null, val method:String?=null, val alias: String?=null, val params:String,
     val executedAt: Date?=null)
