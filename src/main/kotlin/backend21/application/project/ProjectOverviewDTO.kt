package backend21.application.project

data class ProjectOverviewDTO(val histogram: PostHistogramDTO, val topData: TopDataDTO,
    val topRetweets: TweetResultsDTO)
