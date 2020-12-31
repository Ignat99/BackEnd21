package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.PostThroughputByHourAlertStrategy
import backend21.domain.alert.strategy.PostThroughputByMinuteAlertStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class PostThroughputByHourAlertStrategyUnitTest {
    internal lateinit var sut: PostThroughputByHourAlertStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository

    private lateinit var project: Project

    private val date = "2018-05-02T12:12:12"

    private val numPost = 400L

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    private val alias = "post_throughput_hour"




    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=simpleDateFormat.parse("2017-06-07T12:12:12"))
        MockitoAnnotations.initMocks(this)
        sut = PostThroughputByHourAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, numPost, date)
    }


    @Test
    fun test_process_called_correctCallToInnerElasticTweetsService() {
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-05-02T11:12:12", "2018-05-02T12:12:12")).thenReturn(TweetResults(numPost, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProject(project, "2018-05-02T11:12:12", "2018-05-02T12:12:12")
    }



    @Test
    fun test_process_calledWithThresholdAchieved_correctCallToSave() {
        val testNumPosts = 420L
        val showAlert = 1
        doTestWithPreviousAlert(testNumPosts, showAlert)
    }


    @Test
    fun test_process_calledWithThresholdNotAchieved_correctCallToSave() {
        val testNumPosts = 120L
        val showAlert = 0
        doTestWithPreviousAlert(testNumPosts, showAlert)

    }

    private fun doTestWithPreviousAlert(testNumPosts: Long, showAlert: Int) {
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-05-02T11:12:12", "2018-05-02T12:12:12")).thenReturn(TweetResults(testNumPosts, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", alias, simpleDateFormat.parse("2018-05-02T11:12:12"), simpleDateFormat.parse("2018-05-02T12:12:12"), "num=$numPost", showAlert, simpleDateFormat.parse(date)))
    }





}