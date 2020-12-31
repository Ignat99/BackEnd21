package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.SuspiciousMessageStrategy
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

class SuspiciousMessageStrategyUnitTest {
    internal lateinit var sut: SuspiciousMessageStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    private lateinit var project: Project

    private val date = "2018-05-02T12:12:12"

    private val threatLevelMin:Int = 4

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")


    @Before
    fun setUp() {

        project = ProjectMother().testInstance("1", "project1", "2", createdAt=simpleDateFormat.parse("2017-06-07T12:12:12"))
        MockitoAnnotations.initMocks(this)
        sut = SuspiciousMessageStrategy(elasticTweetsService, projectAlertExecutionRepository, project, threatLevelMin, date)
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2017-06-07T12:12:12", date, null, threatLevelMin, 1)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProject(project, "2017-06-07T12:12:12", date, null, threatLevelMin, 1)
    }

    @Test
    fun test_process_calledWithPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "suspicious_message", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level_min=4", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "suspicious_message", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level_min=4", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2018-03-04T12:12:12", date, null, threatLevelMin, 1)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProject(project, "2018-03-04T12:12:12", date, null, threatLevelMin, 1)
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndThreatLevelTweets_correctCallToSave() {
        val total = 3L
        val showAlert = 1
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "suspicious_message", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level_min=4", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "suspicious_message", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level_min=4", 1, simpleDateFormat.parse("2018-02-04T12:12:12"))))
        val from = "2018-03-04T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndNoThreatLevelTweets_correctCallToSave() {
        val total = 0L
        val showAlert = 0
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "suspicious_message", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level_min=4", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "suspicious_message", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level_min=4", 1, simpleDateFormat.parse("2018-02-04T12:12:12"))))
        val from = "2018-03-04T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundButWithThreatLevelTweets_correctCallToSave() {
        val total = 3L
        val showAlert = 1
        val executionResult = Optional.empty<List<ProjectAlertExecution>>()
        val from = "2017-06-07T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFoundButWithNoThreatLevelTweets_correctCallToSave() {
        val total = 0L
        val showAlert = 0
        val executionResult = Optional.empty<List<ProjectAlertExecution>>()
        val from = "2017-06-07T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }

    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,"suspicious_message", project.id!!, "threat_level_min=4")).thenReturn(executionResult)
    }

    private fun exerciseProcessAndVerifySave(executionResult: Optional<List<ProjectAlertExecution>>, from: String, total: Long, showAlert: Int) {
        configureProjectAlertStub(executionResult)
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, from, date, null, threatLevelMin, 1)).thenReturn(TweetResults(total, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", "suspicious_message", simpleDateFormat.parse(from), simpleDateFormat.parse(date), "threat_level_min=4", showAlert, simpleDateFormat.parse(date)))
    }

}