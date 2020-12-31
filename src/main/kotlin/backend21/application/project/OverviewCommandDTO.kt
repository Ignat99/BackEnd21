package backend21.application.project

import java.util.*

data class  OverviewCommandDTO(val projectId:String, val page:Int = 0, val interval:Int = 0,
    val limit:Int=20)
{
    init {
        if (page < 1) throw InvalidPropertiesFormatException("Page must be grater than 0")
    }
}
