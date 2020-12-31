package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.SystemAlertNumPostsStrategy
import backend21.domain.alert.strategy.ThreatLevelThresholdAlertStrategy
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

class ThreatLevelThresholdAlertStrategyUnitTest {
    internal lateinit var sut: ThreatLevelThresholdAlertStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository

    private val threatLevelThreshold = 4L

    private lateinit var project: Project

    private val date = "2018-05-01T12:12:12"


    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2", createdAt=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date))
        MockitoAnnotations.initMocks(this)

        sut = ThreatLevelThresholdAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, threatLevelThreshold, project, date)
    }

    private val alias = "threat_level_threshold"

    private val extravars = "threat_level=4"

    @Test
    fun test_process_called_innerCallTofindByAliasAndProjectIdAndExtravarsOrderByExecutedAtDesc() {
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), extravars, 1)))
        configureProjectAlertStub(executionResult)
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,alias, project.id!!, extravars)
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToElasticService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(threatLevelThreshold, hashMapOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTotalThreadScoresByProject(project)
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundAndNoAchievedThreshold_noCallToAlertExecutionSave() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(3L, hashMapOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.never()).save(ProjectAlertExecution(null, project.id, alias, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), extravars, 1, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundAndAchievedThreshold_callToAlertExecutionSave() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(threatLevelThreshold, hashMapOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, alias, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-01T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), extravars, 1, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }



    @Test
    fun test_process_calledWithPreviousAlertFound_nothingHappens() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", alias, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), extravars, 1))))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.never()).getTotalThreadScoresByProject(project)

    }


    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,alias, project.id!!, extravars)).thenReturn(executionResult)
    }

    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }


    private fun <T> uninitialized(): T = null as T



}