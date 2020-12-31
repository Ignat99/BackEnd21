package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.AlertStrategy
import backend21.domain.alert.strategy.UserMentionAlertStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TweetResults
import backend21.mother.ProjectMother
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.*

open abstract class FeatureInFieldUnitTestBase {
    protected val date = "2018-05-02T12:12:12"
    protected val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    protected lateinit var project:Project
    protected open val feature = ""
    protected open val field = ""
    protected open val varField = ""
    protected open val alias = ""

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    internal lateinit var sut: AlertStrategy



    fun start() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=simpleDateFormat.parse("2017-06-07T12:12:12"))
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectWithFeatureInField(field, feature, project, "2017-06-07T12:12:12", date, limit=20)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProjectWithFeatureInField(field, feature, project, "2017-06-07T12:12:12", date, limit=20)
    }

    @Test
    fun test_process_calledWithPreviousAlertFound_correctCallToInnerElasticTweetsService() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "$varField=$feature", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "$varField=$feature", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectWithFeatureInField(field, feature, project, "2018-03-04T12:12:12", date, limit=20)).thenReturn(TweetResults(3L, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProjectWithFeatureInField(field, feature, project, "2018-03-04T12:12:12", date, limit=20)
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndFeatureInField_correctCallToSave() {
        val total = 3L
        val showAlert = 1
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "$varField=$feature", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "$varField=$feature", 1, simpleDateFormat.parse("2018-02-04T12:12:12"))))
        val from = "2018-03-04T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundAndNoFeatureInField_correctCallToSave() {
        val total = 0L
        val showAlert = 0
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "$varField=$feature", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", alias, simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "$varField=$feature", 1, simpleDateFormat.parse("2018-02-04T12:12:12"))))
        val from = "2018-03-04T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundButWithFeatureInField_correctCallToSave() {
        val total = 3L
        val showAlert = 1
        val executionResult = Optional.empty<List<ProjectAlertExecution>>()
        val from = "2017-06-07T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFoundButWithNoFeatureInField_correctCallToSave() {
        val total = 0L
        val showAlert = 0
        val executionResult = Optional.empty<List<ProjectAlertExecution>>()
        val from = "2017-06-07T12:12:12"
        exerciseProcessAndVerifySave(executionResult, from, total, showAlert)
    }

    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,alias, project.id!!, "$varField=$feature")).thenReturn(executionResult)
    }

    private fun exerciseProcessAndVerifySave(executionResult: Optional<List<ProjectAlertExecution>>, from: String, total: Long, showAlert: Int) {
        configureProjectAlertStub(executionResult)
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectWithFeatureInField(field, feature, project, from, date, limit=20)).thenReturn(TweetResults(total, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, "1", alias, simpleDateFormat.parse(from), simpleDateFormat.parse(date), "$varField=$feature", showAlert, simpleDateFormat.parse(date)))
    }

}