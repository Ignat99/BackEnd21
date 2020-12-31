package backend21.domain

import backend21.infrastructure.repositories.ElasticTweetsRepository
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import backend21.domain.project.interfaces.ProjectRepository
import backend21.domain.socialnetworks.*
import backend21.mother.ProjectMother
import backend21.wrappers.GoogleTranslateWrapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class ElasticTweetsServiceUnitTest {
    internal lateinit var sut: ElasticTweetsService

    @Mock
    internal lateinit var elasticTweetsRepository: ElasticTweetsRepository

    @Mock
    internal lateinit var googleTranslateWrapper: GoogleTranslateWrapper

    private lateinit var project: Project
    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2")
        MockitoAnnotations.initMocks(this)
        sut = ElasticTweetsService(elasticTweetsRepository, googleTranslateWrapper)
    }

    @Test
    fun test_getTotalThreadScoresByProject_called_correctCallToInnerRepository() {
        sut.getTotalThreadScoresByProject(project)
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).findTotalThreadScoresByProject(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"))
    }


    @Test
    fun test_setFlag_called_correctCallToInnerRepository() {
        sut.setFlags(project, "34", listOf("flag1"))
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).setFlags(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), "34", listOf("flag1"))
    }


    @Test
    fun test_removeFlag_called_correctCallToInnerRepository() {
        sut.removeFlags(project, "34", listOf("flag1"))
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).removeFlags(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), "34", listOf("flag1"))
    }



    @Test
    fun test_getTweetsFromProject_called_correctCallToInnerRepository() {
        sut.getTweetsFromProject(project, "2018-01-01T12:12:12", "2018-03-03T14:12:12", "21", 2)
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).findTweetsFromProject(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), "2018-01-01T12:12:12", "2018-03-03T14:12:12", "21", 2)
    }

/*
    @Test
    fun test_getSearchTweetsFromProject_called_correctCallToInnerRepository() {
        sut.getSearchTweetsFromProject(project, "2018-01-01T12:12:12", "2018-03-03T14:12:12", arrayListOf("pena"),
                arrayListOf(), "en", "Israel", "21", 2)
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).findSearchTweetsFromProject(arrayListOf("ala", "alo man",
                "pena"), arrayListOf("ele", "ili"), arrayListOf("apple"), "2018-01-01T12:12:12", "2018-03-03T14:12:12", "en", "Israel", "21", 2)
    }

    @Test
    fun test_getSearchTweetsFromProject_calledWithNullKeywords_correctCallToInnerRepository() {
        sut.getSearchTweetsFromProject(project, "2018-01-01T12:12:12", "2018-03-03T14:12:12", language="en", country="Israel", tweetId="21", threatFilter=2, limit=10)
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).findSearchTweetsFromProject(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), arrayListOf("apple"), "2018-01-01T12:12:12", "2018-03-03T14:12:12", "en", "Israel", "21", 2, 10)
    }
*/

    @Test
    fun test_getProjectOverview_called_correctCallToInnerElasticSearchRepository() {
        val date = exerciseVerifyGetOverview()
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).getHistogramDataByThreatScore(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), 0, date)
    }

    @Test
    fun test_getProjectOverview_called_correctCallToGetTopDataInElasticSearchRepository() {
        exerciseVerifyGetOverview()
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).getTopData(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), "2016-05-06T12:11:11", "2017-05-03T11:11:11", 2)

    }

    private fun exerciseVerifyGetOverview(): Date {
        val date = Date()
        Mockito.`when`(elasticTweetsRepository.getHistogramDataByThreatScore(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), 0, date)).thenReturn(PostHistogram(sortedMapOf()))
        Mockito.`when`(elasticTweetsRepository.getTopData(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), "2016-05-06T12:11:11", "2017-05-03T11:11:11", 2)).thenReturn(TopData(hashMapOf()))
        sut.getOverviewByProject(project, "2016-05-06T12:11:11", "2017-05-03T11:11:11", 2, 0, date)
        return date
    }


    @Test
    fun test_getNumPostsByProject_called_returnSameResultAsInnerRepository() {
        Mockito.`when`(elasticTweetsRepository.findTotalThreadScoresByProject(arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"))).thenReturn(TotalThreadScores(160, hashMapOf(0L to 3L, 1L to 97L, 5L to 60L)))
        val actual = sut.getTotalThreadScoresByProject(project)
        val expected = TotalThreadScores(160, hashMapOf(0L to 3L, 1L to 97L, 5L to 60L))
        Assert.assertEquals(expected, actual)
    }

    @Test(expected = DomainException::class)
    fun test_translateTweet_calledWithUnexistentId_throw() {
        val id = "2345"
        Mockito.`when`(elasticTweetsRepository.findTweetById(id)).thenReturn(null)
        sut.translateTweet(id)
    }

    @Test
    fun test_translateTweet_calledWithExistentId_correctCallToGoogleWrapper() {
        exerciseTranslateTweet()
        Mockito.`verify`(googleTranslateWrapper, Mockito.times(1)).translate("un gran text", "es", "en")
    }

    @Test
    fun test_translateTweet_calledWithExistentId_returnCorrectTranslation() {
        val result = exerciseTranslateTweet()
        Assert.assertEquals("es|a great text", result.sourceLang + "|" + result.text)

    }

    private fun exerciseTranslateTweet():TweetTranslation {
        val id = "32323"
        Mockito.`when`(elasticTweetsRepository.findTweetById(id)).thenReturn(Tweet(id, "2018-01-05T12:12:12", "un gran text", "juan", "juan44", "http://picture.es/pic.jpg", "2323423", 4L, listOf(), "es", 3, 3))
        Mockito.`when`(googleTranslateWrapper.translate("un gran text", "es", "en")).thenReturn("a great text")
        return sut.translateTweet(id)
    }


}
