package backend21.application.project

import backend21.application.user.ProjectTimeFrameDTO
import backend21.infrastructure.sources.NetworkSource
import backend21.domain.BoundingBox
import backend21.domain.DomainException
import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import backend21.domain.project.ProjectTimeFrame
import backend21.domain.project.ProjectTimeFrameService
import backend21.domain.socialnetworks.*
import backend21.mother.ProjectMother
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*


@Suppress("UNCHECKED_CAST")
class ProjectAppServiceUnitTest {
    internal lateinit var sut: ProjectAppService

    @Mock
    internal lateinit var projectService: ProjectService

    @Mock
    internal lateinit var networkSource: NetworkSource

    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectTimeFrameService: ProjectTimeFrameService


    val project = ProjectMother().testInstance("1", "project1", "owner")

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = ProjectAppService(projectService, networkSource, elasticTweetsService, projectTimeFrameService)
    }


    @Test
    fun test_createProject_called_correctCallToInnerCreateProject() {
        configureProjectServiceStubs()
        Mockito.`when`(networkSource.start(any(), any())).thenReturn("")

        sut.createProject(project.toDTO())
        Mockito.`verify`(projectService, times(1)).createProject(any())
    }


    @Test
    fun test_createProject_called_correctCallToNetworkSource() {
        configureProjectServiceStubs()
        sut.createProject(project.toDTO())
        Mockito.`verify`(networkSource, times(1)).start(arrayListOf("twitter"), arrayListOf("mula", "omar"))
    }


    @Test
    fun test_createProject_called_correctCallToCreateProjectTimeframe() {
        configureProjectServiceStubs()
        sut.createProject(project.toDTO())
        Mockito.`verify`(projectTimeFrameService, times(1)).createProjectTimeFrame(any())
    }


    @Test
    fun test_getProjectsByOwner_calledWithPosts_returnCorrectProjectDTO() {
        Mockito.`when`(elasticTweetsService.getTotalThreadScoresByProject(project)).thenReturn(TotalThreadScores(160, hashMapOf(0.toLong() to 3.toLong(), 1.toLong() to 97.toLong(), 5.toLong() to 60.toLong())))
        Mockito.`when`(projectService.getProjectsByOwner("32", "casa")).thenReturn(arrayListOf(project))
        val projectsDTO = sut.getProjectsByOwner("32", "casa")
        var actual = ""
        projectsDTO.forEach { actual += it.name + "|" + it.numPosts + "|" + it.threatLevel + "|"}
        val expected = "project1|160|4|"
        Assert.assertEquals(expected, actual)
    }

    @Test(expected=DomainException::class)
    fun test_deleteProject_calledWithExceptionInInnerService_throw() {
        Mockito.`when`(projectService.getById("32")).thenAnswer( {
            throw DomainException("")
        })
        sut.deleteProject("32", "owner1")
    }

    @Test(expected=CredentialsException::class)
    fun test_deleteProject_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.deleteProject("32", "owner1")
    }

    @Test
    fun test_deleteProject_calleWithProjectWithSameOwner_correctCallToDelete() {
        val project = configureGetByIdProjectStub()
        sut.deleteProject("32", "owner65")
        Mockito.`verify`(projectService, times(1)).deleteProject(project)
    }

    @Test
    fun test_deleteProject_calledWithProjectWithSameOwner_correctCallToNetworkSource() {
        configureGetByIdProjectStub()
        sut.deleteProject("32", "owner65")
        Mockito.`verify`(networkSource, times(1)).start(any(), any())

    }



    @Test(expected=CredentialsException::class)
    fun test_setFlag_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.setFlags("32", "owner1", "33", listOf("flag1"))
    }

    @Test
    fun test_setFlag_calleWithProjectWithSameOwner_correctCallToSetFlag() {
        val project = configureGetByIdProjectStub()
        sut.setFlags("32", "owner65", "33", listOf("flag1"))
        Mockito.`verify`(elasticTweetsService, times(1)).setFlags(project, "33", listOf("flag1"))
    }


    @Test(expected=CredentialsException::class)
    fun test_removeFlag_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.removeFlags("32", "owner1", "33", listOf("flag1"))
    }

    @Test
    fun test_removeFlag_calleWithProjectWithSameOwner_correctCallToRemoveFlag() {
        val project = configureGetByIdProjectStub()
        sut.removeFlags("32", "owner65", "33", listOf("flag1"))
        Mockito.`verify`(elasticTweetsService, times(1)).removeFlags(project, "33", listOf("flag1"))
    }


    @Test
    fun test_updateProject_calledWithProjectWithSameOwner_correctCallToNetworkSource() {
        configureGetByIdProjectStub()
        sut.updateProject("32", "owner65", ProjectDTOUpdate(name = "a name"))
        Mockito.`verify`(networkSource, times(1)).start(any(), any())

    }


    @Test(expected=CredentialsException::class)
    fun test_updateProject_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.updateProject("32", "owner4", ProjectDTOUpdate(name="a name"))
    }


    @Test
    fun test_updateProject_calledWithProjectWithSameOwner_correctCallToUpdateProject() {
        val project = configureGetByIdProjectStub()
        val projectDTOUpdate = ProjectDTOUpdate(name = "a name")
        sut.updateProject("32", "owner65", projectDTOUpdate)
        Mockito.`verify`(projectService, times(1)).updateProject(project, projectDTOUpdate)
    }


    @Test(expected=CredentialsException::class)
    fun test_getTweetsFromProject_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.getTweetsFromProject("owner1", "32")
    }

    @Test
    fun test_getTweetsFromProject_calledWithProjectWithSameOwner_correctCallToInnerTweetService() {
        val project = configureGetByIdProjectStub()
        configureTimeFrameStub()
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project, "2016-07-07T12:12:12", "2017-03-02T11:11:11", "321", 2)).thenReturn(TweetResults(3, arrayListOf()))
        sut.getTweetsFromProject("owner65", "32", "321", 2)
        Mockito.`verify`(elasticTweetsService, times(1)).getTweetsFromProject(project, "2016-07-07T12:12:12", "2017-03-02T11:11:11", "321", 2)
    }

    @Test
    fun test_getTweetsFromProject_calledWithProjectWithSameOwner_correctDTOResult() {
        val project = configureGetByIdProjectStub()
        configureTimeFrameStub()
        Mockito.`when`(elasticTweetsService.getTweetsFromProject(project,"2016-07-07T12:12:12", "2017-03-02T11:11:11", "321", 2)).thenReturn(TweetResults(3, arrayListOf(Tweet("32", "2018-03-03", "a text", "juan", "juanx", "http://image.com", "111", 3, listOf(), "en", 0, 0))))
        val actual = sut.getTweetsFromProject("owner65", "32", "321", 2)
        val expected = TweetResultsDTO(3, arrayListOf(TweetDTO("32", "2018-03-03", "a text", "juan", "juanx", "http://image.com", "111", 3, listOf(), "en", 0, 0)), 3)
        Assert.assertEquals(expected, actual)
    }

    private val boundingBox = BoundingBox(-98.0, 23.0, -94.0, 33.0)

    @Test(expected=CredentialsException::class)
    fun test_getTweetsFromProjectByLocation_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.getTweetsFromProjectByLocation("owner1", "32", "2018-07-07", "2018-06-05", boundingBox)
    }

    @Test
    fun test_getTweetsFromProjectByLocation_calledWithProjectWithSameOwner_correctCallToInnerTweetService() {
        val project = configureGetByIdProjectStub()

        Mockito.`when`(elasticTweetsService.getTweetsFromProjectByLocation(project, "2018-01-02", "2018-03-03", boundingBox, 2)).thenReturn(TweetResults(3, arrayListOf()))
        sut.getTweetsFromProjectByLocation("owner65", "32", "2018-01-02", "2018-03-03", boundingBox, 2)
        Mockito.`verify`(elasticTweetsService, times(1)).getTweetsFromProjectByLocation(project, "2018-01-02", "2018-03-03", boundingBox, 2)
    }

    @Test
    fun test_getTweetsFromProjectByLocation_calledWithProjectWithSameOwner_correctDTOResult() {
        val project = configureGetByIdProjectStub()
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectByLocation(project, "2018-01-02", "2018-03-03", boundingBox, 2)).thenReturn(TweetResults(3, arrayListOf(Tweet("32", "2018-03-03", "a text", "juan", "juanx", "http://image.com", "111", 3, listOf(), "en", 0, 0))))
        val actual = sut.getTweetsFromProjectByLocation("owner65", "32", "2018-01-02", "2018-03-03", boundingBox, 2)
        val expected = TweetResultsDTO(3, arrayListOf(), 0)
        Assert.assertEquals(expected, actual)
    }



    @Test(expected=CredentialsException::class)
    fun test_getTweetsFromProjectByLocationByPlace_calledWithProjectWithDifferentOwner_throw() {
        configureGetByIdProjectStub()
        sut.getTweetsFromProjectByLocationByPlace("owner1", "32", "2018-07-07", "2018-06-05", boundingBox)
    }

    @Test
    fun test_getTweetsFromProjectByLocationByPlace_calledWithProjectWithSameOwner_correctCallToInnerTweetService() {
        val project = configureGetByIdProjectStub()
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectByLocationByPlace(project, "2018-01-02", "2018-03-03", boundingBox, 1, 2)).thenReturn(TopDataItemArray(arrayListOf(TopDataItem("US|New Orleans", 2L), TopDataItem("US|Los Angeles", 5L))))
        sut.getTweetsFromProjectByLocationByPlace("owner65", "32", "2018-01-02", "2018-03-03", boundingBox, 1, 2)
        Mockito.`verify`(elasticTweetsService, times(1)).getTweetsFromProjectByLocationByPlace(project, "2018-01-02", "2018-03-03", boundingBox, 1, 2)
    }

    @Test
    fun test_getTweetsFromProjectByLocationByPlace_calledWithProjectWithSameOwner_correctDTOResult() {
        val project = configureGetByIdProjectStub()
        Mockito.`when`(elasticTweetsService.getTweetsFromProjectByLocationByPlace(project, "2018-01-02", "2018-03-03", boundingBox, 2)).thenReturn(TopDataItemArray(arrayListOf(TopDataItem("US|New Orleans", 2L), TopDataItem("US|Los Angeles", 5L))))
        val actual = sut.getTweetsFromProjectByLocationByPlace("owner65", "32", "2018-01-02", "2018-03-03", boundingBox, 2)
        Assert.assertEquals(arrayListOf("United States", "United States"), actual.data.map { it.country })
    }



    @Test(expected = DomainException::class)
    fun test_createProjectTimeFrame_projectNotExist_throw() {
        Mockito.`when`(projectService.getById("32")).thenAnswer( {
            throw DomainException("")
        })
        sut.createProjectTimeFrame(ProjectTimeFrameDTO("32", Date(), Date(), 0))
    }


    @Test
    fun test_createProjectTimeFrame_projectExist_correctCallToInnerService() {
        Mockito.`when`(projectService.getById("1")).thenReturn(ProjectMother().testInstance(id="1",name="project1", owner="owner3"))
        Mockito.`when`(projectTimeFrameService.createProjectTimeFrame(any())).thenReturn(ProjectTimeFrame("1", Date(), Date(), 1))
        sut.createProjectTimeFrame(ProjectTimeFrameDTO("1", Date(), Date(), 0))
        Mockito.verify(projectTimeFrameService, times(1)).createProjectTimeFrame(any())
    }

    @Test
    fun test_getProjectOverview_calledWithNoTimeframe_callWithDefaultDate() {
        configureNoTimeFrameStub()
        val date = Date()
        exerciseGetOverviewAndMockVerify(date, "2013-02-02T12:12:12")
    }

    private fun configureNoTimeFrameStub() {
        Mockito.`when`(projectTimeFrameService.findById(any())).thenAnswer({
            throw DomainException("")
        })
    }


    @Test
    fun test_getProjectOverview_calledWithTimeFrame_callWithTimeFrameToDate() {

        val (startDate, endDate) = configureTimeFrameStub()
        exerciseGetOverviewAndMockVerify(endDate, "2016-07-07T12:12:12")
    }


    @Test
    @Ignore
    fun test_getProjectOverview_calledWithTimeFrameWithRealtime_callWithTimeFrameToDate() {

        val (startDate, endDate) = configureTimeFrameStub(1)
        exerciseGetOverviewAndMockVerify(endDate, "2016-07-07T12:12:12")
    }




    @Test
    fun test_getTopAnalysis_calledWithNoTimeframe_callWithDefautDate() {
        configureNoTimeFrameStub()
        val project = configureGetByIdProjectStub()
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Date())
        Mockito.`when`(elasticTweetsService.getTopAnalysis(project, "2013-02-02T12:12:12", date, 1)).thenReturn(TopData(hashMapOf()))
        sut.getTopAnalysis("32", "owner65", 1)
        Mockito.verify(elasticTweetsService, times(1)).getTopAnalysis(project, "2013-02-02T12:12:12", date, 1)
    }


    @Test
    fun test_getTopAnalysis_calledWithTimeFrame_callWithTimeFrameToDate() {
        val project = configureGetByIdProjectStub()
        val (startDate, endDate) = configureTimeFrameStub()
        Mockito.`when`(projectTimeFrameService.findById(any())).thenReturn(ProjectTimeFrame("32", startDate, endDate, 0))
        Mockito.`when`(elasticTweetsService.getTopAnalysis(project, "2016-07-07T12:12:12", "2017-03-02T11:11:11", 1)).thenReturn(TopData(hashMapOf()))
        sut.getTopAnalysis("32", "owner65", 1)
        Mockito.verify(elasticTweetsService, times(1)).getTopAnalysis(project, "2016-07-07T12:12:12", "2017-03-02T11:11:11", 1)
    }


    @Test
    fun test_getTimeFrameByOwnerAndId_called_correctCallToInnerProjectService() {
        Mockito.`when`(projectTimeFrameService.findById(any())).thenReturn(ProjectTimeFrame("1", Date(), Date(), 1))
        sut.getTimeFrameByOwnerAndId("owner1", "id1")
        Mockito.verify(projectService, times(1)).getByIdAndOwner("id1", "owner1")
    }



    private fun configureTimeFrameStub(realtime: Int=0) : Pair<Date, Date> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val startDate = simpleDateFormat.parse("2016-07-07T12:12:12")
        val endDate = simpleDateFormat.parse("2017-03-02T11:11:11")
        Mockito.`when`(projectTimeFrameService.findById(any())).thenReturn(ProjectTimeFrame("32", startDate, endDate, realtime))
        return Pair(startDate, endDate)
    }

    private fun configureProjectServiceStubs() {
        Mockito.`when`(projectService.getAllKeywords()).thenReturn(arrayListOf("mula", "omar"))
        val testInstance = ProjectMother().testInstance("1", "project1", "owner")
        Mockito.`when`(projectService.createProject(any())).thenReturn(testInstance)
        Mockito.`when`(projectService.getById("1")).thenReturn(testInstance)
        Mockito.`when`(projectTimeFrameService.createProjectTimeFrame(any())).thenReturn(ProjectTimeFrame("1", testInstance.createdAt, testInstance.createdAt, 1))
    }


    private fun configureGetByIdProjectStub(): Project {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val testInstance = ProjectMother().testInstance("32", "project32", "owner65")
        testInstance.createdAt = simpleDateFormat.parse("2013-02-02T12:12:12")
        Mockito.`when`(projectService.getById("32")).thenReturn(testInstance)

        return testInstance
    }

    private fun exerciseGetOverviewAndMockVerify(date: Date, startDate:String) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val project = configureGetByIdProjectStub()
        Mockito.`when`(elasticTweetsService.getOverviewByProject(project, startDate, simpleDateFormat.format(date), 1, 0, date)).thenReturn(ProjectOverview(PostHistogram(sortedMapOf()), TopData(hashMapOf())))
        sut.getProjectOverview("32", "owner65", 1, 0, date)
        Mockito.verify(elasticTweetsService, times(1)).getOverviewByProject(project, startDate, simpleDateFormat.format(date), 1, 0, date)
    }


    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }


    private fun <T> uninitialized(): T = null as T

}