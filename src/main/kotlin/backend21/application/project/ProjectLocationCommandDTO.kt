package backend21.application.project
import java.util.Date

data class BoundingBoxDTO(val leftLong: Double, val leftLat: Double, val rightLong: Double,
    val rightLat: Double)
data class  ProjectLocationCommandDTO(val projectId:String, val from:Date, val to:Date,
    val boundingBox:BoundingBoxDTO, val page:Int?=1, val limit:Int=20)
