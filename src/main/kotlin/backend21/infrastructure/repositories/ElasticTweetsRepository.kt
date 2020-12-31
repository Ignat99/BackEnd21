package backend21.infrastructure.repositories

import backend21.domain.BoundingBox
import backend21.domain.alert.PostThroughput
import backend21.domain.socialnetworks.TopDataItem
import backend21.domain.socialnetworks.TopDataItemArray
import backend21.domain.socialnetworks.TopData
import backend21.domain.socialnetworks.Tweet
import backend21.domain.socialnetworks.TweetResults
import backend21.domain.socialnetworks.PostHistogram
import backend21.domain.socialnetworks.ThreatLevelCount
import backend21.domain.socialnetworks.TotalThreadScores
import backend21.wrappers.EnvironmentVarWrapper
import backend21.wrappers.VariableVarWrapper
import org.elasticsearch.action.admin.indices.flush.FlushRequest
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.index.reindex.UpdateByQueryAction
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds
import org.elasticsearch.search.sort.SortOrder
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Repository
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

const val INTERVAL_0 = 0
const val INTERVAL_1 = 1
const val INTERVAL_2 = 2
const val INTERVAL_3 = 3
const val INTERVAL_4 = 4
const val TOPFIELD_SIZE = 100
const val CALENDAR_HOUR_6 = -6
const val CALENDAR_DATE_1 = -1
const val CALENDAR_DATE_2 = -2
const val CALENDAR_DATE_7 = -7
const val CALENDAR_DATE_15 = -15
const val TERMQUERY_SIZE = 1000

@Repository
class ElasticTweetsRepository {
    private val environmentVarWrapper: VariableVarWrapper = EnvironmentVarWrapper()
    private lateinit var client: TransportClient

    init {
        val settings = Settings.builder()
                .put("cluster.name", "docker-cluster").put("discovery.type","single-node").build()
        this.client = PreBuiltTransportClient(settings)
                .addTransportAddress(TransportAddress(InetAddress.getByName(environmentVarWrapper
                    .getEnvironmentVar("ELASTIC_HOST")), environmentVarWrapper
                    .getEnvironmentVar("ELASTIC_PORT").toInt()))
    }

    fun findTotalThreadScoresByProject(keywords: List<String>,
        excludedKeywords: List<String>): TotalThreadScores
    {
        val totalQuery = generateTermsQuery(excludedKeywords, keywords)
        val documents = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(0).setQuery(totalQuery)
            .addAggregation(AggregationBuilders.terms("scores").field("analysis.threatScore"))
        val response: SearchResponse = documents.get()
        val totalHits = response.hits.totalHits
        val scores = hashMapOf<Long, Long>()
        val buckets: JSONArray = getAggregationBucket(response, "scores")
        buckets.forEach {
            val item: JSONObject = it as JSONObject
            val dbkey = item["key"]
            val key: Long?
            if (dbkey is Double) key = dbkey.toLong()
            else if (dbkey is Float) key = dbkey.toLong()
            else key = dbkey as Long

            val docCount: Long = item["doc_count"] as Long
            scores[key] = docCount
        }

        return TotalThreadScores(totalHits, scores)
    }

    fun setFlags(keywords: List<String>, excludedKeywords: List<String>, id: String,
        flags: List<String>)
    {
        val script = "if (ctx._source.flags == null) ctx._source.flags = [];ctx._source.flags.addAll(params.hits);ctx._source.flags=ctx._source.flags.stream().distinct().sorted().collect(Collectors.toList())"
        executeUpdateSentenceByTweetId(excludedKeywords, keywords, id, script, flags)
    }


    fun removeFlags(keywords: List<String>, excludedKeywords: List<String>, id: String,
        flags: List<String>)
    {
        val script = "if (ctx._source.flags != null) { ctx._source.flags.removeAll(params.hits);ctx._source.flags=ctx._source.flags.stream().distinct().sorted().collect(Collectors.toList()) }"
        executeUpdateSentenceByTweetId(excludedKeywords, keywords, id, script, flags)
    }

    fun findTweetsFromProjectByFlag(keywords: List<String>, excludedKeywords: List<String>, fromDate: String,
        toDate: String, flagName: String, tweetId: String? = null, threatFilter: Int? = null,
        limit: Int = 20): TweetResults
    {
        val totalQuery = generateTermsQuery(excludedKeywords, keywords)
        val finalQuery = totalQuery?.must(QueryBuilders.termsQuery("flags.keyword", flagName))
        val document = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(limit).setQuery(finalQuery)
            .addSort("createdAt", SortOrder.DESC)
        print(document)

        val response: SearchResponse = document.get()
        return toTweetResults(response)
    }

    fun findTweetsFromProject(keywords: List<String>, excludedKeywords: List<String>, from: String,
        to: String, tweetId: String? = null, threatFilter: Int? = null,
        limit: Int = 20): TweetResults
    {
        val documents = prepareTweetsQuery(excludedKeywords, keywords, limit, tweetId,
            threatFilter, from, to)
        val response: SearchResponse = documents.get()
        return toTweetResults(response)
    }


    fun findTweetsFromProjectWithFeatureInField(field: String, feature: String,
        keywords: List<String>, excludedKeywords: List<String>, from: String, to: String,
        tweetId: String? = null, threatFilter: Int? = null, limit: Int = 5): TweetResults
    {
        val condition1 = generateTermsQuery(excludedKeywords, keywords)
        val condition2 = generateTermsQuery(arrayListOf(), arrayListOf(feature), field)
        return searchWithCondition(limit, condition1?.must(condition2), tweetId, threatFilter,
            from, to)
    }


    fun findTweetsFromProjectAndXMentionsY(screenName1: String, screenName2: String,
        keywords: List<String>, excludedKeywords: List<String>, from: String, to: String,
        tweetId: String? = null, threatFilter: Int? = null, limit: Int = 20): TweetResults
    {
        val condition1 = generateTermsQuery(excludedKeywords, keywords)
        val condition2 = generateTermsQuery(arrayListOf(), arrayListOf(screenName1),
            "user.screenName")
        val condition3 = generateTermsQuery(arrayListOf(), arrayListOf(screenName2),
            "userMentionEntities.screenName")
        return searchWithCondition(limit, condition1?.must(condition2)?.must(condition3),
            tweetId, threatFilter, from, to)
    }

    fun findTweetsFromIds(ids: List<String>, limit: Int = 20): TweetResults {
        val document = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(limit).setQuery(QueryBuilders
            .termsQuery("id", ids))
        val response: SearchResponse = document.get()
        return toTweetResults(response)
    }

    fun findRetweetById(id: String): Tweet? {
        val document = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setQuery(QueryBuilders
            .termQuery("retweetedStatus.id", id))
        val response: SearchResponse = document.get()
        val json = responseToJSON(response)
        val results: JSONObject = json["hits"] as JSONObject

        val hits: JSONArray = results["hits"] as JSONArray
        var analysis: JSONObject = JSONObject(hashMapOf<String, String>())
        val retweets = hits.map {
            val item: JSONObject = it as JSONObject
            val source: JSONObject = item["_source"] as JSONObject
            analysis = source["analysis"] as JSONObject
            val ret: JSONObject = source["retweetedStatus"] as JSONObject
            ret
        }.toSet()
        if (retweets.isEmpty()) return null
        val first = retweets.first()

        first.set("analysis", analysis)
        val tweetResult = mapTweet(first)

        return tweetResult
    }


    fun findTweetById(id: String): Tweet? {
        val document = prepareSearchById(id)
        val response: SearchResponse = document.get()
        val tweetResult = toTweetResults(response)
        return tweetResult.sources.find { it.id == id }
    }

    private fun prepareSearchById(id: String) =
            client.prepareSearch(environmentVarWrapper.getEnvironmentVar("ELASTIC_INDEX"))
            .setQuery(QueryBuilders.termQuery("id", id))


    fun getPostThroughputByMinute(keywords: List<String>, excludedKeywords: List<String>,
        from: String, to: String): PostThroughput
    {
        val documents = prepareTweetsQuery(excludedKeywords, keywords, 0, null, null, from, to,
            "yyyy-MM-dd'T'HH:mm:ss")
        documents.addAggregation(AggregationBuilders.dateHistogram("posts_by_date")
            .field("createdAt").format("yyyy-MM-dd'T'HH:mm:ss")
            .dateHistogramInterval(DateHistogramInterval("1m"))
            .extendedBounds(ExtendedBounds(from, to)).minDocCount(1))
        val response: SearchResponse = documents.get()
        val buckets = getAggregationBucket(response, "posts_by_date")
        val hashValues = sortedMapOf<String, Long>()
        buckets.forEach { thr ->
            val throughtputItem: JSONObject = thr as JSONObject
            hashValues[throughtputItem["key_as_string"] as String] = throughtputItem["doc_count"] as Long

        }

        return PostThroughput(hashValues)
    }


    fun getHistogramDataByThreatScore(keywords: List<String>, excludedKeywords: List<String>,
        interval: Int = 0, endDate: Date = Date()): PostHistogram
    {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val from = manageStartDate(interval, endDate)
        var fromStr = simpleDateFormat.format(from) + "T00:00:00"
        val simpleDateFormatWithHours = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val toStr = simpleDateFormatWithHours.format(endDate)
        val documents = prepareTweetsQuery(excludedKeywords, keywords, 0, null, null, fromStr,
            toStr, "yyyy-MM-dd'T'HH:mm:ss")
        val dateHistogramInterval: String? = if (interval < 2) "1d"
        else "1h"
        if (interval == INTERVAL_4) fromStr = simpleDateFormatWithHours.format(from)
        documents.addAggregation(AggregationBuilders.dateRange("date_range").addRange(fromStr,
            toStr).field("createdAt").format("yyyy-MM-dd'T'HH:mm:ss")
            .subAggregation(AggregationBuilders.dateHistogram("posts_by_date")
            .field("createdAt").format("yyyy-MM-dd'T'HH:mm:ss")
            .dateHistogramInterval(DateHistogramInterval(dateHistogramInterval))
            .extendedBounds(ExtendedBounds(fromStr, toStr))
            .subAggregation(AggregationBuilders.terms("scores").field("analysis.threatScore"))))
        val response: SearchResponse = documents.get()
        val buckets = getAggregationBucket(response, "date_range")
        val hashValues = sortedMapOf<String, ArrayList<ThreatLevelCount>>()
        buckets.forEach {
            val item: JSONObject = it as JSONObject
            val postsByDate: JSONObject = item["posts_by_date"] as JSONObject
            val innerBuckets: JSONArray = postsByDate["buckets"] as JSONArray
            innerBuckets.forEach { com ->
                val innerItem: JSONObject = com as JSONObject
                val keyAsString: String = innerItem["key_as_string"] as String
                val scores: JSONObject = innerItem["scores"] as JSONObject
                val scoreBuckets: JSONArray = scores["buckets"] as JSONArray
                val scoreHash = arrayListOf<ThreatLevelCount>()
                scoreBuckets.forEach { scr ->
                    val scoreItem: JSONObject = scr as JSONObject
                    scoreHash.add(ThreatLevelCount(scoreItem["key"] as Long, scoreItem["doc_count"] as Long))
                }
                hashValues[keyAsString] = scoreHash
            }
        }
        return PostHistogram(hashValues)
    }





    fun getAvancedSearchCombos():HashMap<String, List<String>> {
        val result = hashMapOf<String, List<String>>()
        val documents = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(0)
        documents.addAggregation(AggregationBuilders.terms("languages").field("lang.keyword"))
           .addAggregation(AggregationBuilders.terms("countries").field("user.location"))
                .addAggregation(AggregationBuilders.terms("sources").field("_type"))

        val response: SearchResponse = documents.get()
        val languages = getAggregationBucket(response, "languages")
        val countries = getAggregationBucket(response, "countries")
        val sources = getAggregationBucket(response, "sources")
        result["languages"] = languages.map { (it as JSONObject)["key"] as String}
        result["countries"] = countries.map { (it as JSONObject)["key"] as String}
        result["sources"] = sources.map { (it as JSONObject)["key"] as String}
        return result
    }




    fun getTopData(keywords: List<String>, excludedKeywords: List<String>, from: String,
        to: String, page: Int = 1, limit: Int = 5): TopData
    {

        return getTopComposite(keywords, excludedKeywords, from, to, page, limit,
            listOf(Pair("users", "user.screenName"),
                Pair("hashtags", "analysis.hashtags.text"), 
		Pair("concepts",  "analysis.concepts.concept"), 
		Pair("topics", "analysis.topics.topic"),
                Pair("keyIdeas", "analysis.keyIdeas.keyIdea"),
                Pair("retweets",  "retweetedStatus.id")))
    }




    fun getTopAnalysis(keywords: List<String>, excludedKeywords: List<String>, from: String, to: String, threatFilter: Int? = null, page: Int = 1, limit: Int = 10): TopData {
        return getTopComposite(keywords, excludedKeywords, from, to, page, limit,
            listOf(Pair("users", "user.screenName"),
                Pair("hashtags", "analysis.hashtags.text"),
                Pair("topics", "analysis.topics.topic"),
                Pair("keyIdeas", "analysis.keyIdeas.keyIdea"),
                Pair("concepts",  "analysis.concepts.concept"),
                Pair("entities", "analysis.entities.entity")), threatFilter)
    }


    //fun getTopRetweets(keywords: List<String>, excludedKeywords: List<String>, from: String, to: String, threatFilter: Int? = null, page: Int = 1, limit: Int = 10): TopDataItemArray = exerciseTopByField(excludedKeywords, keywords, from, to, "retweetedStatus.id", limit ,page, threatFilter)




    private fun getTopComposite(keywords: List<String>, excludedKeywords: List<String>,
        from: String, to: String, page: Int = 1, limit: Int = 5, data:List<Pair<String, String>>,
        threatFilter: Int? = null): TopData
    {
        val items: HashMap<String, TopDataItemArray> = hashMapOf()
        data.forEach {
            items[it.first] = exerciseTopByField(excludedKeywords, keywords, from, to, it.second,
                limit ,page, threatFilter)
        }
        return TopData(items)
    }



    fun exerciseTopByField(excludedKeywords: List<String>, keywords: List<String>, from: String,
        to: String, topField: String, limit: Int, page: Int,
        threatFilter: Int? = null): TopDataItemArray
    {
        val size = TOPFIELD_SIZE

        //val documents = client.prepareSearch(environmentVarWrapper.getEnvironmentVar("ELASTIC_INDEX")).setSize(limit)


        val totalQuery = generateTermsQuery(excludedKeywords, keywords)
        val finalQuery = totalQuery?.must(RangeQueryBuilder("analysis.threatScore")
            .gte(threatFilter?:0))
        val documents = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(limit).setQuery(finalQuery)
            .addSort("createdAt", SortOrder.DESC)
        documents.setPostFilter(getRangeBoolQueryBuilder(null, threatFilter, from, to,
            "yyyy-MM-dd'T'HH:mm:ss"))




        //documents.setQuery(BoolQueryBuilder().must(RangeQueryBuilder("analysis.threatScore").gte(threatFilter?:0)))
        documents.addAggregation(AggregationBuilders.dateRange("date_range").addRange(from, to)
            .field("createdAt").format("yyyy-MM-dd'T'HH:mm:ss")
            .subAggregation(AggregationBuilders.terms("top").field(topField).size(size)))
        val response: SearchResponse = documents.get()

        val buckets = getAggregationBucket(response, "date_range")
        val topDataArray = arrayListOf<TopDataItem>()
        buckets.forEach {
            val item: JSONObject = it as JSONObject
            val topUsers: JSONObject = item["top"] as JSONObject
            val userBuckets: JSONArray = topUsers["buckets"] as JSONArray
            val initialIndex = limit * (page - 1)
            var finalIndex = limit * page
            if (finalIndex > userBuckets.size) finalIndex = userBuckets.size
            if (initialIndex >= userBuckets.size) return TopDataItemArray(arrayListOf())
            val subUserBuckets = userBuckets.subList(initialIndex, finalIndex)
            subUserBuckets.forEach { scr ->
                val subUserItem: JSONObject = scr as JSONObject
                topDataArray.add(TopDataItem(subUserItem["key"].toString(),
                    subUserItem["doc_count"] as Long))
            }

        }
        return TopDataItemArray(topDataArray)
    }


    private fun manageStartDate(interval: Int, endDate: Date): Date {
        val cal = Calendar.getInstance();
        cal.time = endDate
        if (interval == INTERVAL_4) cal.add(Calendar.HOUR, CALENDAR_HOUR_6)
        else if (interval == INTERVAL_3) cal.add(Calendar.DATE, CALENDAR_DATE_1)
        else if (interval == INTERVAL_2) cal.add(Calendar.DATE, CALENDAR_DATE_2)
        else if (interval == INTERVAL_1) cal.add(Calendar.DATE, CALENDAR_DATE_7)
        else if (interval == INTERVAL_0) cal.add(Calendar.DATE, CALENDAR_DATE_15)
        else throw InvalidPropertiesFormatException("Invalid interval option: $interval")
        return cal.time
    }


    private fun prepareTweetsQuery(excludedKeywords: List<String>, keywords: List<String>,
        limit: Int, tweetId: String?, threatFilter: Int?, from: String, to: String,
        format: String = "yyyy-MM-dd'T'HH:mm:ss"): SearchRequestBuilder
    {
        val totalQuery = generateTermsQuery(excludedKeywords, keywords)
        val documents = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(limit).setQuery(totalQuery)
            .addSort("createdAt", SortOrder.DESC)
        documents.setPostFilter(getRangeBoolQueryBuilder(tweetId, threatFilter, from, to, format))
        return documents
    }


    private fun getRangeBoolQueryBuilder(tweetId: String?, threatFilter: Int? = null, from: String, to: String, format: String): BoolQueryBuilder {
        val rangeQueryBuilder = RangeQueryBuilder("createdAt").from(from).to(to).format(format)
        val queryBuilder: BoolQueryBuilder = BoolQueryBuilder().must(rangeQueryBuilder)
        if (tweetId != null) queryBuilder.must(RangeQueryBuilder("id").lt(tweetId))
        if (threatFilter != null) queryBuilder.must(RangeQueryBuilder("analysis.threatScore")
            .gte(threatFilter))
        return queryBuilder
    }


    private fun generateTermsQuery(excludedKeywords: List<String>, keywords: List<String>,
        field: String = "unifiedText", tmpKeywords: List<String> = emptyList()): BoolQueryBuilder?
    {
        val excludedTerms = arrayListOf<String>()
        var excludedSubquery = BoolQueryBuilder()
        excludedKeywords.forEach {
            if (it.contains(" ")) excludedSubquery = excludedSubquery
                .mustNot(QueryBuilders.matchPhraseQuery(field, it))
            else excludedTerms.add(it)
        }
        if (excludedTerms.isNotEmpty()) {
            excludedSubquery = excludedSubquery.mustNot(QueryBuilders.termsQuery(field,
                excludedTerms))
        }

        val includedTerms = arrayListOf<String>()
        var includedSubquery = BoolQueryBuilder()
        keywords.forEach {
            if (it.contains(" ")) includedSubquery = includedSubquery.should(QueryBuilders
                .matchPhraseQuery(field, it))
            else includedTerms.add(it)
        }
        if (includedTerms.isNotEmpty()) {
            includedSubquery = includedSubquery.should(QueryBuilders.termsQuery(field,
                    includedTerms))
        }

        var totalQuery = excludedSubquery.must(includedSubquery)

        tmpKeywords.forEach {
            var includedSubquery2 = BoolQueryBuilder()
            includedSubquery2 = includedSubquery2.must(QueryBuilders.matchPhraseQuery(field, it))
            totalQuery = totalQuery.must(includedSubquery2)
        }

        return totalQuery
    }

    private fun getAggregationBucket(response: SearchResponse,
        aggregationName: String): JSONArray
   {
        val json = responseToJSON(response)
        val aggs: JSONObject = json["aggregations"] as JSONObject
        val aggregation: JSONObject = aggs[aggregationName] as JSONObject
        val buckets: JSONArray = aggregation["buckets"] as JSONArray
        return buckets
    }

    private fun responseToJSON(response: SearchResponse): JSONObject {
        val builder = XContentFactory.jsonBuilder()
        response.toXContent(builder, ToXContent.EMPTY_PARAMS)
        val parser = JSONParser()
        val json = parser.parse(builder.string()) as JSONObject
        return json
    }

    private fun toTweetResults(response: SearchResponse): TweetResults {
        val json = responseToJSON(response)
        val results: JSONObject = json["hits"] as JSONObject
        val total: Long = results["total"] as Long
        val hits: JSONArray = results["hits"] as JSONArray
        val tweetResult: ArrayList<Tweet> = hits.map {
            val item: JSONObject = it as JSONObject
            val data: JSONObject = item["_source"] as JSONObject
            mapTweet(data)
        } as ArrayList<Tweet>
        val sourceResults = TweetResults(total, tweetResult)
        return sourceResults
    }

    private fun mapTweet(data: JSONObject): Tweet {
        val user: JSONObject = data["user"] as JSONObject
        val analysis: JSONObject = data["analysis"] as JSONObject
        val flags: List<String> = if (data["flags"] == null) listOf()
            else data["flags"] as List<String>
        return Tweet(data["id"] as String, data["createdAt"] as String,
            data["text"] as String, user["name"] as String, user["screenName"] as String,
            user["profileImageUrl"] as String, user["id"] as String,
            analysis["threatScore"] as Long, flags, data["lang"] as String, 0, 0)
    }

    fun findSearchTweetsFromProject(keywords: List<String>, tmpKeywords: List<String>, excludedKeywords: List<String>,
        fromDate: String, toDate: String, language: String? = null, country: String? = null,
            tweetId: String? = null, threatFilter: Int? = null, limit: Int = 20): TweetResults
    {
        var condition1 = generateTermsQuery(excludedKeywords, keywords, "unifiedText", tmpKeywords)
        if (language != null) condition1 = condition1?.must(generateTermsQuery(arrayListOf(),
            arrayListOf(language), "lang"))
        if (country != null) condition1 = condition1?.must(generateTermsQuery(arrayListOf(),
            arrayListOf(country), "place.country"))
        return searchWithCondition(limit, condition1, tweetId, threatFilter, fromDate, toDate)
    }

    private fun searchWithCondition(limit: Int, wholeCondition: BoolQueryBuilder?, tweetId: String?, threatFilter: Int?, from: String, to: String): TweetResults {
        val document = client.prepareSearch(environmentVarWrapper.getEnvironmentVar("ELASTIC_INDEX")).setQuery(wholeCondition).addSort("createdAt", SortOrder.DESC)
        document.setPostFilter(getRangeBoolQueryBuilder(tweetId, threatFilter, from, to, "yyyy-MM-dd'T'HH:mm:ss"))

        val response: SearchResponse = document.get()
        return toTweetResults(response)
    }

    fun findTweetsFromProjectByLocation(keywords: List<String>, excludedKeywords: List<String>,
        fromDate: String, toDate: String, boundingBox: BoundingBox, limit: Int): TweetResults
    {
        val totalQuery = generateTermsQuery(excludedKeywords, keywords)
        val finalQuery = totalQuery?.filter(GeoBoundingBoxQueryBuilder("coordinates")
            .setCorners(boundingBox.leftLat, boundingBox.leftLong, boundingBox.rightLat,
                boundingBox.rightLong))
        val document = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(limit).setQuery(finalQuery)
            .addSort("createdAt", SortOrder.DESC)
        val rangeQueryBuilder = RangeQueryBuilder("createdAt").from(fromDate).to(toDate)
            .format("yyyy-MM-dd'T'HH:mm:ss")

        document.setPostFilter(rangeQueryBuilder)
        //document.setPostFilter(GeoBoundingBoxQueryBuilder("coordinates").setCorners(boundingBox.leftLat, boundingBox.leftLong, boundingBox.rightLat, boundingBox.rightLong))


        val response: SearchResponse = document.get()
        return toTweetResults(response)
    }

    fun findTweetsFromProjectByLocationByPlace(keywords: List<String>,
        excludedKeywords: List<String>, fromDate: String, toDate: String,
        boundingBox: BoundingBox, page: Int = 1, limit: Int = 10): TopDataItemArray
    {
        val totalQuery = generateTermsQuery(excludedKeywords, keywords)
        val finalQuery = totalQuery?.filter(GeoBoundingBoxQueryBuilder("coordinates")
            .setCorners(boundingBox.leftLat, boundingBox.leftLong, boundingBox.rightLat,
            boundingBox.rightLong))
        val documents = client.prepareSearch(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX")).setSize(0).setQuery(finalQuery)
            .addSort("createdAt", SortOrder.DESC)
        documents.addAggregation(AggregationBuilders.dateRange("date_range")
            .addRange(fromDate, toDate).field("createdAt").format("yyyy-MM-dd'T'HH:mm:ss")
                .subAggregation(AggregationBuilders.terms("places").field("geoname.countryCode")
                .subAggregation(AggregationBuilders.terms("places2").field("geoname.name"))))



        val response: SearchResponse = documents.get()

        val buckets = getAggregationBucket(response, "date_range")
        val topDataArray = arrayListOf<TopDataItem>()
        buckets.forEach {
            val item: JSONObject = it as JSONObject
            val countries: JSONObject = item["places"] as JSONObject
            val countryBuckets: JSONArray = countries["buckets"] as JSONArray
            countryBuckets.forEach {
                countryItem->
                val countIt: JSONObject = countryItem as JSONObject
                val cities: JSONObject = countIt["places2"] as JSONObject
                val cityBuckets: JSONArray = cities["buckets"] as JSONArray
                cityBuckets.forEach { scr ->
                    val subItem: JSONObject = scr as JSONObject
                    topDataArray.add(TopDataItem(
                        "" + countryItem["key"] + "|" + subItem["key"].toString(),
                        subItem["doc_count"] as Long))
                }
            }
        }
        val initialIndex = limit * (page - 1)
        var finalIndex = limit * page
        if (finalIndex > topDataArray.size) finalIndex = topDataArray.size
        if (initialIndex >= topDataArray.size) return TopDataItemArray(arrayListOf())
        val subList = ArrayList(topDataArray.subList(initialIndex, finalIndex))
        return TopDataItemArray(subList)
    }


    private fun executeUpdateSentenceByTweetId(excludedKeywords: List<String>,
        keywords: List<String>, id: String, script: String, flags: List<String>)
    {
        val updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client)
        updateByQuery.source(environmentVarWrapper.getEnvironmentVar("ELASTIC_INDEX"))
                .filter(generateTermsQuery(excludedKeywords, keywords)?.must(QueryBuilders
            .termQuery("id", id)))
                .size(TERMQUERY_SIZE)
                .script(Script(ScriptType.INLINE, "painless", script, mapOf("hits" to flags)))
        print(updateByQuery)
        val result = updateByQuery.get()
        if (result.updated == 0L) throw NoSuchElementException("Tweet with id $id doesn't exist")
        client.admin().indices().flush(FlushRequest(environmentVarWrapper
            .getEnvironmentVar("ELASTIC_INDEX"))).actionGet()
    }


    companion object {
//        private val LOG = LoggerFactory.getLogger(ElasticTweetsRepository::class.java)
        private val LOG = LogManager.getLogger(ElasticTweetsRepository::class.java)
    }

}
