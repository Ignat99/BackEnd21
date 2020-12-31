package backend21.application.project

data class TopDataItemDTO(val item: String, val numPosts: Long)
data class TopDataItemArrayDTO(val topInfo: ArrayList<TopDataItemDTO>)
data class TopDataDTO(val data: HashMap<String, TopDataItemArrayDTO>)
