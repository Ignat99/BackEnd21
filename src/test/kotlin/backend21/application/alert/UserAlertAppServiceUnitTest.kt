package backend21.application.alert

import backend21.domain.DomainException
import backend21.domain.alert.UserAlert
import backend21.domain.alert.UserAlertService
import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.project.ProjectService
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class UserAlertAppServiceUnitTest {

    internal lateinit var sut: UserAlertAppService

    @Mock
    internal lateinit var projectService: ProjectService

    @Mock
    internal lateinit var userAlertService: UserAlertService



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = UserAlertAppService(projectService, userAlertService)
    }


    @Test(expected=DomainException::class)
    fun test_updateUserAlert_calledWithInexistentUserAlert_throw() {
        Mockito.`when`(userAlertService.getById("22")).thenReturn(null)
        sut.updateUserAlert("22", "3", UserAlertDTOUpdate(1,1))
    }

    @Test(expected=DomainException::class)
    fun test_updateUserAlert_calledWithUserAlertThatDontMatchWithUserId_throw() {
        Mockito.`when`(userAlertService.getById("22")).thenReturn(UserAlert("22", "24", "8", "alias", hashMapOf()))
        sut.updateUserAlert("22", "3", UserAlertDTOUpdate(1,1))
    }



    @Test
    fun test_updateUserAlert_calledWithCorrectUserAlert_correctCallToInnerUpdateUserAlert() {
        val userAlert = UserAlert("22", "24", "3", "alias", hashMapOf())
        Mockito.`when`(userAlertService.getById("22")).thenReturn(userAlert)
        val userAlertUpdate = UserAlertDTOUpdate(1, 1)
        sut.updateUserAlert("22", "3", userAlertUpdate)
        Mockito.`verify`(userAlertService, times(1)).updateUserAlert(userAlert, userAlertUpdate)
    }



    @Test(expected = DomainException::class)
    fun test_creatUserAlert_projectNotExist_throw() {
        Mockito.`when`(projectService.getById("11")).thenAnswer( {
            throw DomainException("")
        })
        sut.createUserAlert("22", UserAlertDTO("32", "11", "user_mention", type="user"))
    }


    @Test
    fun test_createUserAlert_projectExist_correctCallToInnerService() {
        configureStubsForCreate()
        sut.createUserAlert("owner3", UserAlertDTO("32", "11", "user_mention", type="user"))
        Mockito.verify(userAlertService, Mockito.times(1)).createUserAlert(any())
    }



    @Test(expected= CredentialsException::class)
    fun test_createUserAlert_projectExistButIncorrectOwner_throw() {
        configureStubsForCreate()
        sut.createUserAlert("owner34", UserAlertDTO("32", "11", "user_mention", type="user"))
        Mockito.verify(userAlertService, Mockito.times(1)).createUserAlert(any())
    }


    private fun configureStubsForCreate() {
        Mockito.`when`(projectService.getById("11")).thenReturn(ProjectMother().testInstance(id = "11", name = "project1", owner = "owner3"))
        Mockito.`when`(userAlertService.createUserAlert(any())).thenReturn(UserAlert("32", "11", "owner3", "user_mention", type="user"))
    }

    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }


    private fun <T> uninitialized(): T = null as T
}