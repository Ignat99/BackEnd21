package backend21.domain.socialnetworks

import backend21.application.project.TweetDTO


data class Tweet(val id: String, val createdAt: String, val text:String, val authorName: String,
    val authorScreenName: String, val authorProfileImageUrl: String, val authorId: String,
    val threatScore: Long, val flags: List<String>, val lang: String, val likes: Int,
    var retweets: Long) 
{
    fun toDTO(): TweetDTO = TweetDTO(this.id, this.createdAt, this.text, this.authorName,
        this.authorScreenName, this.authorProfileImageUrl, this.authorId, this.threatScore,
        this.flags, this.lang, this.likes, this.retweets)
}
