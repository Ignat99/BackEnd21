package backend21.domain.alert

import backend21.application.alert.ListProjectAlertExecutionsDTO

data class ListProjectAlertExecutions(val unread: ArrayList<ProjectAlertExecution>,
    val read:ArrayList<ProjectAlertExecution>)
{
    fun toDTO(): ListProjectAlertExecutionsDTO = ListProjectAlertExecutionsDTO(this.unread
        .map { it.toDTO() } as ArrayList, this.read.map { it.toDTO() } as ArrayList)
}
