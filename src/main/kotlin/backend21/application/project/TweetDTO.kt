package backend21.application.project

data class TweetDTO(val id: String, val createdAt: String, val text:String,
    val authorName: String, val authorScreenName: String, val authorProfileImageUrl: String,
    val authorId: String, val threatScore: Long, val flags: List<String>, val lang: String,
    val likes: Int, val retweets: Long)
