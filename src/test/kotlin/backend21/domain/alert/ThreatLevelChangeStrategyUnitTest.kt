package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.SystemAlertNumPostsStrategy
import backend21.domain.alert.strategy.ThreatLevelChangeStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.PostHistogram
import backend21.domain.socialnetworks.TotalThreadScores
import backend21.domain.socialnetworks.TweetResults
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class ThreatLevelChangeStrategyUnitTest {
    internal lateinit var sut: ThreatLevelChangeStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    private lateinit var project: Project

    private val date = "2018-05-02T12:12:12"


    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-01T12:12:12"))
        MockitoAnnotations.initMocks(this)
        sut = ThreatLevelChangeStrategy(elasticTweetsService, projectAlertExecutionRepository, project, date)
    }



    @Test
    fun test_process_calledWithNoPreviousAlertFoundAndAchievedThreshold_callToAlertExecutionSave() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(9, hashMapOf(1L to 8L, 5L to 1L)))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, "change_threat_level", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-01T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), "threat_level=3", 0, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }

    @Test
    fun test_process_calledWithPreviousAlertFoundWithSameThreatLevel_callToAlertExecutionSaveWithNoShowAlert() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "change_threat_level", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level=3", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "change_threat_level", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level=3", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(6, hashMapOf(3L to 6L)))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, "change_threat_level", simpleDateFormat.parse("2018-05-01T12:12:12"), simpleDateFormat.parse("2018-05-02T12:12:12"), "threat_level=3", 0, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundWithDifferentThreatLevel_callToAlertExecutionSaveWithShowAlert() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "change_threat_level", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level=3", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "change_threat_level", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level=3", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(4, hashMapOf(4L to 4L)))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, "change_threat_level", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-01T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), "threat_level=4,old=3", 1, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundWithThreatLevelAndOldThreatLevelAndDontChange_callToAlertExecutionSaveWithNoShowAlert() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "change_threat_level", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level=4,old=3", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "change_threat_level", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level=3", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(4, hashMapOf(4L to 4L)))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, "change_threat_level", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-01T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), "threat_level=4", 0, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }


    @Test
    fun test_process_calledWithPreviousAlertFoundWithThreatLevelAndOldThreatLevelAndChange_callToAlertExecutionSaveWithShowAlert() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "change_threat_level", simpleDateFormat.parse("2018-03-01T12:12:12"), simpleDateFormat.parse("2018-03-04T12:12:12"), "threat_level=4,old=3", 0, simpleDateFormat.parse("2018-03-04T12:12:12")), ProjectAlertExecution("2", "1", "change_threat_level", simpleDateFormat.parse("2018-02-01T12:12:12"), simpleDateFormat.parse("2018-02-04T12:12:12"), "threat_level=3", 1, simpleDateFormat.parse("2018-02-04T12:12:12")))))
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(4, hashMapOf(5L to 4L)))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, "change_threat_level", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-01T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), "threat_level=5,old=4", 1, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }


    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,"change_threat_level", project.id!!, "")).thenReturn(executionResult)
    }

    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }


    private fun <T> uninitialized(): T = null as T



}