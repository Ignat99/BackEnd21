package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.PercentIncrementByDayStrategy

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

class PercentIncrementByDayStrategyUnitTest {
    internal lateinit var sut: PercentIncrementByDayStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    private lateinit var project: Project

    private val date = "2018-05-02T12:12:12"

    private val ratio:Float = 0.1.toFloat()

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")


    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=simpleDateFormat.parse("2017-06-07T12:12:12"))
        MockitoAnnotations.initMocks(this)
        sut = PercentIncrementByDayStrategy(elasticTweetsService, projectAlertExecutionRepository, project, ratio, date)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-05-02T00:00:00", "2018-05-02T23:59:59", limit=1)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProject(project, "2018-05-02T00:00:00", "2018-05-02T23:59:59", limit=1)
    }


    @Test
    fun test_process_calledWithPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "increase_num_posts", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "ratio=$ratio", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "increase_num_posts", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "ratio=$ratio", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        exerciseProcess(5L, 3L)
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProject(project, "2018-05-02T00:00:00", "2018-05-02T23:59:59", limit=1)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToSave() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-05-02T00:00:00", "2018-05-02T23:59:59", limit=1)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", "increase_num_posts", simpleDateFormat.parse("2018-05-02T00:00:00"), simpleDateFormat.parse("2018-05-02T23:59:59"), "ratio=$ratio,num=3", 0, simpleDateFormat.parse(date)))
    }

    @Test
    fun test_process_calledWithPreviousAlertFoundAndRatioAchieved_correctCallToSave() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "increase_num_posts", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "ratio=$ratio", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "increase_num_posts", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "ratio=$ratio", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        exerciseProcess(5L, 3L)
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", "increase_num_posts", simpleDateFormat.parse("2018-05-02T00:00:00"), simpleDateFormat.parse("2018-05-02T23:59:59"), "ratio=$ratio,num=5", 1, simpleDateFormat.parse(date)))
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndRatioNotAchieved_correctCallToSave() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "increase_num_posts", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "ratio=$ratio", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "increase_num_posts", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "ratio=$ratio", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        exerciseProcess(4300L, 4000L)
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", "increase_num_posts", simpleDateFormat.parse("2018-05-02T00:00:00"), simpleDateFormat.parse("2018-05-02T23:59:59"), "ratio=$ratio,num=4300", 0, simpleDateFormat.parse(date)))
    }


    private fun exerciseProcess(total:Long, totalYesterday: Long) {
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-05-02T00:00:00", "2018-05-02T23:59:59", limit = 1)).thenReturn(TweetResults(total, arrayListOf()))
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-05-01T00:00:00", "2018-05-01T23:59:59", limit = 1)).thenReturn(TweetResults(totalYesterday, arrayListOf()))
        sut.process()
    }


    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,"increase_num_posts", project.id!!, "ratio=$ratio")).thenReturn(executionResult)
    }


}