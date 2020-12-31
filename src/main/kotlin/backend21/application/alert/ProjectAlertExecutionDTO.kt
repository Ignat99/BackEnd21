package backend21.application.alert

import java.util.*

data class ProjectAlertExecutionDTO(val id: String?=null, val projectId: String?=null,
    val alias: String?=null,
    val fromDate: Date?=Date(),
    val toDate: Date?=Date(),
    val extravars: String?=null,
    val showAlert: Int?=1,
    val executedAt: Date?= Date(),
    val visited:Int?=0)
