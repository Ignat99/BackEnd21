package backend21.domain

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.UserAlert
import backend21.domain.alert.UserAlertService
import backend21.domain.alert.UserExecutionAlertService
import backend21.domain.email.EmailService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import java.util.*

class UserExecutionAlertServiceUnitTest: ExecutionAlertTestBase() {



    @Mock
    internal lateinit var userAlertService: UserAlertService



    internal lateinit var sut: UserExecutionAlertService

    @Mock
    internal lateinit var emailService: EmailService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = UserExecutionAlertService(userAlertService, alertLocator, projectService, emailService)
    }



    @Test
    fun test_process_calledWith2ProjectsAnd3Alerts_correctCallToInnerProcess() {
        Mockito.`when`(userAlertService.getActiveAlertsByProject("1")).thenReturn(arrayListOf(UserAlert("1", "11", "3", "alias1", hashMapOf("a" to "1")), UserAlert("2", "22", "3", "alias2", hashMapOf("b" to "2")), UserAlert("3", "33", "3", "alias3", hashMapOf("c" to "3"))))
        Mockito.`when`(userAlertService.getActiveAlertsByProject("2")).thenReturn(arrayListOf(UserAlert("3", "33", "3", "alias3", hashMapOf("c" to "3"))))
        val (xMentionYMock, threatLevelChangeMock, systemAlertMock) = configureTest()
        sut.process()
        Mockito.`verify`(xMentionYMock, times(1)).process()
        Mockito.`verify`(threatLevelChangeMock, times(1)).process()
        Mockito.`verify`(systemAlertMock, times(2)).process()
    }


    @Test
    fun test_process_calledWithAlertThatRequiresMail_correctCallToInnerEmailService() {
        Mockito.`when`(userAlertService.getActiveAlertsByProject("2")).thenReturn(arrayListOf(UserAlert("3", "33", "3", "alias3", hashMapOf("c" to "3"), email=1)))
        val projectAlertExecution = exerciseProcess()
        Mockito.`verify`(emailService, times(1)).alert(projectAlertExecution, project2, "http://localhost:8080/api/")
    }



    @Test
    fun test_process_calledWithAlertThatNotRequiresMail_correctCallToInnerEmailService() {
        Mockito.`when`(userAlertService.getActiveAlertsByProject("2")).thenReturn(arrayListOf(UserAlert("3", "33", "3", "alias3", hashMapOf("c" to "3"), email=0)))
        val projectAlertExecution = exerciseProcess()
        Mockito.`verify`(emailService, never()).alert(projectAlertExecution, project2, "http://localhost:8080/api/")
    }


    @Test
    fun test_process_calledWithAlertThatRequiresMailButIsNotAlert_correctCallToInnerEmailService() {
        Mockito.`when`(userAlertService.getActiveAlertsByProject("2")).thenReturn(arrayListOf(UserAlert("3", "33", "3", "alias3", hashMapOf("c" to "3"), email=1)))
        val projectAlertExecution = exerciseProcess(0)
        Mockito.`verify`(emailService, never()).alert(projectAlertExecution, project2, "http://localhost:8080/api/")
    }


    @Test
    fun test_process_calledWithAlertThatNoReturnProjectAlertExecution_correctCallToInnerEmailService() {
        Mockito.`when`(userAlertService.getActiveAlertsByProject("2")).thenReturn(arrayListOf(UserAlert("3", "33", "3", "alias3", hashMapOf("c" to "3"), email=1)))
        val (xMentionYMock, threatLevelChangeMock, systemAlertMock) = configureTest()
        Mockito.`when`(systemAlertMock.process()).thenReturn(null)
        sut.process()
        Mockito.`verify`(emailService, never()).alert(any(), any(), any())
    }


    private fun exerciseProcess(showAlert:Int=1): ProjectAlertExecution {
        val (xMentionYMock, threatLevelChangeMock, systemAlertMock) = configureTest()
        val projectAlertExecution = ProjectAlertExecution("2", "2", "alias3", Date(), Date(), "user=juan", showAlert, Date())
        Mockito.`when`(systemAlertMock.process()).thenReturn(projectAlertExecution)
        sut.process()
        return projectAlertExecution
    }

    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T

}