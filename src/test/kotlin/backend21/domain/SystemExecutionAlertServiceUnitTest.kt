package backend21.domain

import backend21.domain.alert.AlertLocator
import backend21.domain.alert.SystemAlert
import backend21.domain.alert.SystemExecutionAlertService
import backend21.domain.alert.interfaces.SystemAlertRepository
import backend21.domain.alert.strategy.SystemAlertNumPostsStrategy
import backend21.domain.alert.strategy.ThreatLevelChangeStrategy
import backend21.domain.alert.strategy.XMentionYAlertStrategy
import backend21.domain.email.EmailService
import backend21.domain.project.ProjectService
import backend21.mother.ProjectMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired

class SystemExecutionAlertServiceUnitTest: ExecutionAlertTestBase() {


    @Mock
    internal lateinit var systemAlertRepository: SystemAlertRepository



    internal lateinit var sut: SystemExecutionAlertService

    @Mock
    internal lateinit var emailService: EmailService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = SystemExecutionAlertService(systemAlertRepository, alertLocator, projectService, emailService)
    }



    @Test
    fun test_process_calledWith2ProjectsAnd3Alerts_correctCallToInnerProcess() {
        Mockito.`when`(systemAlertRepository.findAll()).thenReturn(arrayListOf(SystemAlert("1", "alias1", hashMapOf("a" to "1")), SystemAlert("2", "alias2", hashMapOf("b" to "2")), SystemAlert("3", "alias3", hashMapOf("c" to "3"))))
        val (xMentionYMock, threatLevelChangeMock, systemAlertMock) = configureTest()
        sut.process()
        Mockito.`verify`(xMentionYMock, times(2)).process()
        Mockito.`verify`(threatLevelChangeMock, times(2)).process()
        Mockito.`verify`(systemAlertMock, times(2)).process()
    }


}