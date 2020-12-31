package backend21.application.project
import java.util.Date
data class  ProjectCommandDTO(val projectId:String, val from:Date, val to:Date,
    val tweetId: String?=null, val threatFilter:Int?=null, val limit:Int=20)
