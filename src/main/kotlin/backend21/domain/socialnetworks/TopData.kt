package backend21.domain.socialnetworks

import backend21.application.project.TopDataDTO
import backend21.application.project.TopDataItemArrayDTO
import backend21.application.project.TopDataItemDTO

data class TopDataItem(val item: String, val numPosts: Long) {
    fun toDTO() = TopDataItemDTO(item, numPosts)
}
data class TopDataItemArray(val topInfo: ArrayList<TopDataItem>) {
    fun getItems(): ArrayList<String> = topInfo.map { it.item } as ArrayList<String>
    fun getTotal(item:String): Long? = topInfo.find { it.item == item }?.numPosts
    fun toDTO(): TopDataItemArrayDTO {
        val topInfoDTO = topInfo.map { it.toDTO() } as ArrayList<TopDataItemDTO>
        return TopDataItemArrayDTO(topInfoDTO)
    }
}
data class TopData(val data: HashMap<String, TopDataItemArray>) {
    fun toDTO(): TopDataDTO {
        val copyData = hashMapOf<String, TopDataItemArrayDTO>()
        data.keys.forEach {
            if (it != "retweets") {
                copyData[it] = data[it]!!.toDTO()
            }
        }
        return TopDataDTO(copyData)
    }
}
