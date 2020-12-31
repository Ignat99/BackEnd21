package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.AlertStrategy

import backend21.domain.alert.strategy.XMentionYAlertStrategy
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

class XMentionYAlertStrategyUnitTest {
    private val date = "2018-05-02T12:12:12"
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private lateinit var project: Project
    private val screenName1 = "juan"
    private val screenName2 = "luis"
    private val alias = "x_mentions_y"


    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    internal lateinit var sut: AlertStrategy


    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=simpleDateFormat.parse("2017-06-07T12:12:12"))
        MockitoAnnotations.initMocks(this)
        sut = XMentionYAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, screenName1, screenName2, date)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectAndXMentionsY(screenName1, screenName2, project, "2017-06-07T12:12:12", date, limit=20)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProjectAndXMentionsY(screenName1, screenName2, project, "2017-06-07T12:12:12", date, limit=20)
    }

    @Test
    fun test_process_calledWithPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "screenname1=$screenName1,screenname2=$screenName2", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "screename1=$screenName1,screenname2=$screenName2", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectAndXMentionsY(screenName1, screenName2, project, "2018-03-04T12:12:12", date, limit=20)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProjectAndXMentionsY(screenName1, screenName2, project, "2018-03-04T12:12:12", date, limit=20)
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndXMentionsY_correctCallToSave() {
        val total = 3L
        val showAlert = 1
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "screenname1=$screenName1,screenname2=$screenName2", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "screenname1=$screenName1,screenname2=$screenName2", 1, simpleDateFormat.parse("2018-02-04T12:12:12"))))
        val from = "2018-03-04T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndNoXMentionsY_correctCallToSave() {
        val total = 0L
        val showAlert = 0
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "screenname1=$screenName1,screenname2=$screenName2", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "screenname1=$screenName1,screenname2=$screenName2", 1, simpleDateFormat.parse("2018-02-04T12:12:12"))))
        val from = "2018-03-04T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundButXMentionsY_correctCallToSave() {
        val total = 3L
        val showAlert = 1
        val executionResult = Optional.empty<List<ProjectAlertExecution>>()
        val from = "2017-06-07T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFoundButNoXMentionsY_correctCallToSave() {
        val total = 0L
        val showAlert = 0
        val executionResult = Optional.empty<List<ProjectAlertExecution>>()
        val from = "2017-06-07T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }

    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,alias, project.id!!, "screenname1=$screenName1,screenname2=$screenName2")).thenReturn(executionResult)
    }

    private fun exerciseProcessAndVerifySave(executionResult: Optional<List<ProjectAlertExecution>>, from: String, total: Long, showAlert: Int) {
        configureProjectAlertStub(executionResult)
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectAndXMentionsY(screenName1, screenName2, project, from, date, limit=20)).thenReturn(TweetResults(total, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", alias, simpleDateFormat.parse(from), simpleDateFormat.parse(date), "screenname1=$screenName1,screenname2=$screenName2", showAlert, simpleDateFormat.parse(date)))
    }

}