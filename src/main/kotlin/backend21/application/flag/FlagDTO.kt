package backend21.application.flag

import javax.persistence.Id

data class FlagDTO(
    var id: String? = null,
    val name: String?=null,
    val projectId: String?=null,
    val active: Int =1)
