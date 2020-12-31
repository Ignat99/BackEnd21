package backend21.domain

import backend21.domain.alert.AlertLocator
import backend21.domain.alert.strategy.SystemAlertNumPostsStrategy
import backend21.domain.alert.strategy.ThreatLevelChangeStrategy
import backend21.domain.alert.strategy.XMentionYAlertStrategy
import backend21.domain.project.ProjectService
import backend21.mother.ProjectMother
import org.mockito.Mock
import org.mockito.Mockito

open class ExecutionAlertTestBase {

    @Mock
    protected lateinit var projectService: ProjectService

    @Mock
    protected lateinit var alertLocator: AlertLocator

    protected val project1 = ProjectMother().testInstance("1", "name1", "owen1")
    protected val project2 = ProjectMother().testInstance("2", "name2", "owen2")

    protected fun configureTest(): Triple<XMentionYAlertStrategy, ThreatLevelChangeStrategy, SystemAlertNumPostsStrategy> {
        val xMentionYMock = Mockito.mock(XMentionYAlertStrategy::class.java)
        val threatLevelChangeMock = Mockito.mock(ThreatLevelChangeStrategy::class.java)
        val systemAlertMock = Mockito.mock(SystemAlertNumPostsStrategy::class.java)


        Mockito.`when`(projectService.getProjects()).thenReturn(arrayListOf(project1, project2))
        Mockito.`when`(alertLocator.getAlertStrategy("alias1", hashMapOf("a" to "1"), project1)).thenReturn(xMentionYMock)
        Mockito.`when`(alertLocator.getAlertStrategy("alias2", hashMapOf("b" to "2"), project1)).thenReturn(threatLevelChangeMock)
        Mockito.`when`(alertLocator.getAlertStrategy("alias3", hashMapOf("c" to "3"), project1)).thenReturn(systemAlertMock)
        Mockito.`when`(alertLocator.getAlertStrategy("alias1", hashMapOf("a" to "1"), project2)).thenReturn(xMentionYMock)
        Mockito.`when`(alertLocator.getAlertStrategy("alias2", hashMapOf("b" to "2"), project2)).thenReturn(threatLevelChangeMock)
        Mockito.`when`(alertLocator.getAlertStrategy("alias3", hashMapOf("c" to "3"), project2)).thenReturn(systemAlertMock)

        return Triple(xMentionYMock, threatLevelChangeMock, systemAlertMock)
    }
}