package backend21.application.flag


import backend21.domain.DomainException
import backend21.domain.flag.Flag
import backend21.domain.flag.FlagService
import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.project.ProjectService
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class FlagAppServiceUnitTest {

    internal lateinit var sut: FlagAppService

    @Mock
    internal lateinit var flagService: FlagService
    @Mock
    internal lateinit var projectService: ProjectService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = FlagAppService(flagService, projectService)
    }



    @Test(expected = DomainException::class)
    fun test_createFlag_projectNotExist_throw() {
        Mockito.`when`(projectService.getById("11")).thenAnswer( {
            throw DomainException("")
        })
        sut.createFlag("22", FlagDTO("32", "a name", "11"))
    }


    @Test
    fun test_createFlag_projectExist_correctCallToInnerService() {
        configureStubsForCreate()
        sut.createFlag("owner3", FlagDTO("32", "a name", "11"))
        Mockito.verify(flagService, Mockito.times(1)).createFlag(any())
    }



    @Test(expected= CredentialsException::class)
    fun test_createFlag_projectExistButIncorrectOwner_throw() {
        configureStubsForCreate()
        sut.createFlag("owner34", FlagDTO("32", "a name", "11"))
        Mockito.verify(flagService, Mockito.times(1)).createFlag(any())
    }


    @Test(expected = CredentialsException::class)
    fun test_getFlags_projectExistsButIncorrectOwner_throw() {
        configureStubsForGet()
        sut.getFlags("owner34", "34")
    }

    @Test
    fun test_getFlags_projectExistsAndCorrectOwner_correctCallToInnerService() {
        configureStubsForGet()
        sut.getFlags("owner3", "34")
        Mockito.verify(flagService, Mockito.times(1)).getFlagsByProjectId("34")
    }


    private fun configureStubsForCreate() {
        Mockito.`when`(projectService.getById("11")).thenReturn(ProjectMother().testInstance(id = "11", name = "project1", owner = "owner3"))
        Mockito.`when`(flagService.createFlag(any())).thenReturn(Flag("32", "a name", "11"))
    }


    private fun configureStubsForGet() {
        Mockito.`when`(projectService.getById("34")).thenReturn(ProjectMother().testInstance(id = "34", name = "project1", owner = "owner3"))
        Mockito.`when`(flagService.getFlagsByProjectId(any())).thenReturn(listOf(Flag("32", "a name", "34")))
    }

    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }


    private fun <T> uninitialized(): T = null as T
}