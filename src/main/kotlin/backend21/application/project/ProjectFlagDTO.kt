package backend21.application.project

import java.util.*

data class ProjectFlagDTO(val projectId:String, val flagName:String, val tweetId: String?=null,
    val threatFilter:Int?=null, val limit:Int=20)
