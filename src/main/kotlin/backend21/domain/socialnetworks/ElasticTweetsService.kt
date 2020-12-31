package backend21.domain.socialnetworks

import backend21.infrastructure.repositories.ElasticTweetsRepository
import backend21.domain.BoundingBox
import backend21.domain.DomainException
import backend21.domain.alert.PostThroughput
import backend21.domain.project.Project
import backend21.wrappers.GoogleTranslateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

const val PAGE_5 = 5


@Service
class ElasticTweetsService @Autowired constructor(
    private val elasticTweetsRepository: ElasticTweetsRepository,
    private val googleTranslateWrapper: GoogleTranslateWrapper) {

    fun getTotalThreadScoresByProject(project: Project): TotalThreadScores
        = elasticTweetsRepository.findTotalThreadScoresByProject(
            project.getKeywords(), project.getExcludedKeywords())


    fun setFlags(project: Project, id: String, flags: List<String>){
        elasticTweetsRepository.setFlags(
            project.getKeywords(), project.getExcludedKeywords(), id, flags)
    }


    fun removeFlags(project: Project, id: String, flags: List<String>){
        elasticTweetsRepository.removeFlags(project.getKeywords(), project.getExcludedKeywords(),
            id, flags)
    }


    fun getAvancedSearchCombos(): HashMap<String, List<String>>
        = elasticTweetsRepository.getAvancedSearchCombos()


    fun getTweetsFromProject(project: Project, from: String, to: String, tweetId:String?=null,
        threatFilter:Int?=null,
        limit:Int=20):TweetResults = elasticTweetsRepository.findTweetsFromProject(
            project.getKeywords(), project.getExcludedKeywords(), from, to, tweetId,
            threatFilter, limit)

    fun getTweetsFromProjectByFlag(project: Project, flagName: String, from: String, to: String, tweetId:String?=null,
        threatFilter:Int?=null,
        limit:Int=20):TweetResults = elasticTweetsRepository.findTweetsFromProjectByFlag(
            project.getKeywords(), project.getExcludedKeywords(), from, to, flagName, tweetId,
            threatFilter, limit)

    fun getTweetsFromProjectByLocation(project:Project, fromDate: String, toDate: String,
        boundingBox: BoundingBox, limit: Int=1): TweetResults = elasticTweetsRepository
            .findTweetsFromProjectByLocation(project.getKeywords(), project.getExcludedKeywords(),
            fromDate, toDate, boundingBox, limit)


    fun getTweetsFromProjectByLocationByPlace(project:Project, fromDate: String, toDate: String,
        boundingBox: BoundingBox, page: Int = 1, limit: Int=10): TopDataItemArray =
        elasticTweetsRepository.findTweetsFromProjectByLocationByPlace(
            project.getKeywords(), project.getExcludedKeywords(), fromDate, toDate, 
                boundingBox, page, limit)

    fun getTweetsFromProjectWithFeatureInField(field:String, feature: String, project: Project,
        from: String, to:String, tweetId:String?=null, threatFilter:Int?=null,
        limit:Int=20):TweetResults = elasticTweetsRepository
        .findTweetsFromProjectWithFeatureInField(
            field, feature, project.getKeywords(),
            project.getExcludedKeywords(), from, to, tweetId, threatFilter, limit)

    fun getTweetsFromProjectAndXMentionsY(screenName1: String, screenName2: String,
        project: Project, from: String, to: String, tweetId:String?=null,
        threatFilter:Int?=null, limit: Int = 20):TweetResults = elasticTweetsRepository
           .findTweetsFromProjectAndXMentionsY(screenName1, screenName2,
           project.getKeywords(), project.getExcludedKeywords(), from, to, tweetId, threatFilter,
          limit)

    fun getThroughputByMinute(project: Project, from: String, to: String): PostThroughput =
        elasticTweetsRepository.getPostThroughputByMinute(
            project.getKeywords(), project.getExcludedKeywords(), from, to)

    fun getOverviewByProject(project: Project, from: String, to: String,
        page: Int, interval:Int=0, date:Date= Date()): ProjectOverview 
    {
        val keywords:List<String> = project.getKeywords()
        val excludedKeywords:List<String> = project.getExcludedKeywords()
        val histogram = elasticTweetsRepository.getHistogramDataByThreatScore(keywords,
            excludedKeywords, interval, date)
        val topData = elasticTweetsRepository.getTopData(keywords, excludedKeywords, from,
            to, page, PAGE_5)
        return ProjectOverview(histogram, topData)
    }


    fun getTopAnalysis(project: Project, from: String, to: String, page: Int,
        threatFilter:Int?=null, limit:Int=10): TopData = elasticTweetsRepository.getTopAnalysis(
        project.getKeywords(), project.getExcludedKeywords(), from, to, threatFilter, page, limit)

    fun getTopUsers(project: Project, from: String, to: String, threatFilter: Int? = null,
        page: Int = 1, limit: Int = 10): TopDataItemArray = elasticTweetsRepository
            .exerciseTopByField(project.getExcludedKeywords(), project.getKeywords(),
            from, to, "user.screenName", limit ,page, threatFilter)
    fun getTopHashTags(project: Project, from: String, to: String,
        threatFilter: Int? = null, page: Int = 1, limit: Int = 10): TopDataItemArray =
        elasticTweetsRepository.exerciseTopByField(
            project.getExcludedKeywords(), project.getKeywords(), from, to, 
            "analysis.hashtags.text", limit ,page, threatFilter)
    fun getTopTopics(project: Project, from: String, to: String, threatFilter: Int? = null,
        page: Int = 1, limit: Int = 10): TopDataItemArray = elasticTweetsRepository
            .exerciseTopByField(project.getExcludedKeywords(), project.getKeywords(), from, to,
            "analysis.topics.topic", limit ,page, threatFilter)
    fun getTopKeyIdeas(project: Project, from: String, to: String,
        threatFilter: Int? = null, page: Int = 1, limit: Int = 10): TopDataItemArray =
        elasticTweetsRepository.exerciseTopByField(
            project.getExcludedKeywords(), project.getKeywords(), from, to, 
            "analysis.keyIdeas.keyIdea", limit ,page, threatFilter)
    fun getTopConcepts(project: Project, from: String, to: String, threatFilter: Int? = null,
        page: Int = 1, limit: Int = 10): TopDataItemArray =
        elasticTweetsRepository.exerciseTopByField(
            project.getExcludedKeywords(), project.getKeywords(), from, to, 
            "analysis.concepts.concept", limit ,page, threatFilter)
    fun getTopEntities(project: Project, from: String, to: String, threatFilter: Int? = null,
        page: Int = 1, limit: Int = 10): TopDataItemArray = elasticTweetsRepository
            .exerciseTopByField(project.getExcludedKeywords(), project.getKeywords(), from, to,
            "analysis.entities.entity", limit ,page, threatFilter)



    fun getTweetsByIds(ids: ArrayList<String>): TweetResults = 
        elasticTweetsRepository.findTweetsFromIds(ids)


    fun translateTweet(id: String): TweetTranslation {
        val tweet: Tweet = elasticTweetsRepository.findTweetById(id) ?: 
            throw DomainException("Tweet with id $id doesn't exist")
        val text = googleTranslateWrapper.translate(tweet.text, tweet.lang, "en")
        return TweetTranslation(tweet.lang, text)
    }

    fun getSearchTweetsFromProject(project: Project, fromDate: String, toDate: String,
        keywords: List<String> = arrayListOf(),
        excludedKeywords: List<String> = arrayListOf(),
        language: String?=null, country: String?=null,
        tweetId: String?=null, threatFilter: Int?=null, limit: Int?=20): TweetResults
    {
        val totalKeywords = project.getKeywords()
        val totalExcludedKeywords = project.getExcludedKeywords().plus(excludedKeywords)
        return elasticTweetsRepository.findSearchTweetsFromProject(totalKeywords, keywords, totalExcludedKeywords, fromDate, toDate, language, country, tweetId, threatFilter, limit!!)
    }

}
