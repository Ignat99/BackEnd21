package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.AlertConstants
import backend21.domain.alert.strategy.SystemAlertNumPostsStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.infrastructure.repositories.ElasticTweetsRepository
import backend21.domain.socialnetworks.PostHistogram
import backend21.domain.socialnetworks.TweetResults
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class SystemAlertNumPostsStrategyUnitTest {
    internal lateinit var sut: SystemAlertNumPostsStrategy

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService


    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    @Mock
    internal lateinit var elasticTweetsRepository: ElasticTweetsRepository


    private val numPosts = 100000L

    private lateinit var project: Project

    private val date = "2018-05-02T12:12:12"


    @Before
    fun setUp() {
        project = ProjectMother().testInstance("1", "project1", "2")
        MockitoAnnotations.initMocks(this)

        sut = SystemAlertNumPostsStrategy(elasticTweetsService, projectAlertExecutionRepository, numPosts, project, date)
    }

    @Test
    fun test_process_called_innerCallTofindByAliasAndProjectIdAndExtravarsOrderByExecutedAtDesc() {
        val executionResult: Optional<List<ProjectAlertExecution>> = Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "num_posts", Date(), Date(), "num=100000", 1)))
        configureProjectAlertStub(executionResult)
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,"num_posts", project.id!!, "num=100000")
    }

    @Test
    fun test_process_calledWithNoPreviousAlertFound_correctCallToElasticService() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(project.createdAt),"2018-05-02T12:12:12")).thenReturn(TweetResults(30, arrayListOf()))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.times(1)).getTweetsFromProject(project, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(project.createdAt), "2018-05-02T12:12:12")
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundAndNoAchievedThreshold_noCallToAlertExecutionSave() {
        configureProjectAlertStub(Optional.empty())
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(project.createdAt),"2018-05-02T12:12:12")).thenReturn(TweetResults(30, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.never()).save(ProjectAlertExecution(null, project.id, AlertConstants.SYSTEM_ALIAS, project.createdAt, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), "num=${this.numPosts}", 1, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }


    @Test
    fun test_process_calledWithNoPreviousAlertFoundAndAchievedThreshold_callToAlertExecutionSave() {
        configureProjectAlertStub(Optional.empty())
        val from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(project.createdAt)
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, from,"2018-05-02T12:12:12")).thenReturn(TweetResults(100001, arrayListOf()))
        sut.process()
        Mockito.`verify`(projectAlertExecutionRepository, Mockito.times(1)).save(ProjectAlertExecution(null, project.id, AlertConstants.SYSTEM_ALIAS, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(from), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date), "num=${this.numPosts}", 1, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)))
    }
/*
    @Test
    fun test_getTweets_normal() {
        configureProjectAlertStub(Optional.empty())
        val from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(project.createdAt)
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, from,"2018-01-01T12:12:12")).thenReturn(TweetResults(100001, arrayListOf()))
        sut.getTweets("2018-01-01T12:12:12","2018-03-03T14:12:12", "21", 2)
        Mockito.`verify`(elasticTweetsRepository, Mockito.times(1)).findSearchTweetsFromProject(arrayListOf("ala", "alo man"), arrayListOf("ele","ili"), arrayListOf(""), "2018-01-01T12:12:12", "2018-03-03T14:12:12", "en", "Israel", "21", 2, 10)
    }
*/

    @Test
    fun test_process_calledWithPreviousAlertFound_nothingHappens() {
        configureProjectAlertStub(Optional.of(arrayListOf(ProjectAlertExecution("1", "1", "num_posts", project.createdAt, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-05-02T12:12:12"), "num=100000", 1))))
        sut.process()
        Mockito.`verify`(elasticTweetsService, Mockito.never()).getTweetsFromProject(project, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(project.createdAt), "2018-05-02T12:12:12")

    }


    private fun configureProjectAlertStub(executionResult: Optional<List<ProjectAlertExecution>>) {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0,"num_posts", project.id!!, "num=100000")).thenReturn(executionResult)
    }

    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }


    private fun <T> uninitialized(): T = null as T



}
