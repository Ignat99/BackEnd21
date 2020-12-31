package backend21.application.project

data class TweetResultsDTO(val total:Long, val sources:ArrayList<TweetDTO>, val threatLevel: Int)
