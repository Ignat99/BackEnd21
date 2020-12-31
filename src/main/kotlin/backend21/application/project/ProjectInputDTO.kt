package backend21.application.project

import java.util.*

data class ProjectInputDTO(val projectId:String, val tweetId: String?=null,
    val threatFilter:Int?=null, val limit:Int=20)
