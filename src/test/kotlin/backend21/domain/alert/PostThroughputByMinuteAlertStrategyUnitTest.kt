package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.PostThroughputByMinuteAlertStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class PostThroughputByMinuteAlertStrategyUnitTest {
    internal lateinit var sut: PostThroughputByMinuteAlertStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository

    private lateinit var project: Project

    private val date = "2018-05-02T12:12:12"

    private val numPost = 400L

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    private val alias = "post_throughput_minute"

    private val extravars = "num=$numPost"


    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=simpleDateFormat.parse("2017-06-07T12:12:12"))
        MockitoAnnotations.initMocks(this)
        sut = PostThroughputByMinuteAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, numPost, date)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getThroughputByMinute(project, "2018-05-02T12:02:12", "2018-05-02T12:12:12")).thenReturn(PostThroughput(sortedMapOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getThroughputByMinute(project, "2018-05-02T12:02:12", "2018-05-02T12:12:12")
    }


    @Test
    fun test_process_calledWithPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), extravars, 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), extravars, 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getThroughputByMinute(project, "2018-03-04T12:12:12", "2018-05-02T12:12:12")).thenReturn(PostThroughput(sortedMapOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getThroughputByMinute(project, "2018-03-04T12:12:12", "2018-05-02T12:12:12")
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToSave() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getThroughputByMinute(project, "2018-05-02T12:02:12", "2018-05-02T12:12:12")).thenReturn(PostThroughput(sortedMapOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", alias, simpleDateFormat.parse("2018-05-02T12:02:12"), simpleDateFormat.parse("2018-05-02T12:12:12"), "num=$numPost,date_throughput=[]", 0, simpleDateFormat.parse(date)))
    }

    @Test
    fun test_process_calledWithPreviousAlertFoundAndThresholdAchieved_correctCallToSave() {
        val testNumPosts = 420L
        val showAlert = 1
        doTestWithPreviousAlert(testNumPosts, showAlert, setOf("2018-04-06T12:12:12"))
    }




    @Test
    fun test_process_calledWithPreviousAlertFoundAndThresholdNotAchieved_correctCallToSave() {
        val testNumPosts = 120L
        val showAlert = 0
        doTestWithPreviousAlert(testNumPosts, showAlert, setOf())

    }

    private fun doTestWithPreviousAlert(testNumPosts: Long, showAlert: Int, dataThroughput: Set<String>) {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), extravars, 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), extravars, 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getThroughputByMinute(project, "2018-03-04T12:12:12", "2018-05-02T12:12:12")).thenReturn(PostThroughput(sortedMapOf(Pair("2018-04-06T12:12:12", testNumPosts))))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", alias, simpleDateFormat.parse("2018-03-04T12:12:12"), simpleDateFormat.parse("2018-05-02T12:12:12"), "num=$numPost,date_throughput=$dataThroughput", showAlert, simpleDateFormat.parse(date)))
    }


    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,alias, project.id!!, extravars)).thenReturn(executionResult)
    }


}