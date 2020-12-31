package backend21.domain.socialnetworks

data class ProjectOverview(val histogram: PostHistogram, val topData: TopData) {
    fun getTopRetweets(): TopDataItemArray? = topData.data["retweets"]
}
