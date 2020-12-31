package backend21.application.alert


data class ListProjectAlertExecutionsDTO(val unread: ArrayList<ProjectAlertExecutionDTO>, 
    val read:ArrayList<ProjectAlertExecutionDTO>)
