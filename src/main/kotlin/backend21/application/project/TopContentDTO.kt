package backend21.application.project

import java.util.*

data class TopContentDTO(val projectId:String, val threatFilter:Int?=null, val page:Int = 0,
    val limit:Int=20, val date: Date=Date())
