package backend21.domain.socialnetworks

import backend21.application.project.TweetDTO
import backend21.application.project.TweetResultsDTO

const val L_0 : Long = 0
const val L_1 : Long = 1
const val L_2 : Long = 2
const val L_3 : Long = 3
const val L_4 : Long = 4
const val L_5 : Long = 5

data class TweetResults(val total: Long, val sources: ArrayList<Tweet>) {
    fun setRetweets(id: String, total: Long) {
        val tweet = sources.find { it.id == id }
        tweet?.retweets = total
    }
    fun toDTO():TweetResultsDTO {
        this.sources.sortBy { it.retweets }
        return TweetResultsDTO(this.total, this.sources.map { it.toDTO() } as ArrayList<TweetDTO>,
            threatScore())
    }


    fun threatScore(): Int {
        val threatScoreHash = hashMapOf(L_0 to L_0, L_1 to L_0, L_2 to L_0, L_3 to L_0, L_4 to L_0, L_5 to L_0)
        sources.forEach {
           threatScoreHash[it.threatScore] = threatScoreHash[it.threatScore]!! + 1
        }
        val totalThreadScores = TotalThreadScores(this.total, threatScoreHash)
        return totalThreadScores.getThreatLevel()
    }

}
