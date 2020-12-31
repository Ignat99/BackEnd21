package backend21.domain

import backend21.domain.alert.AlertLocator
import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.ProjectAlertExecutionService
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.AlertStrategy
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import backend21.mother.ProjectMother
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.springframework.dao.EmptyResultDataAccessException
import java.text.SimpleDateFormat
import java.util.*

class ProjectAlertExecutionServiceUnitTest {

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository


    @Mock
    internal lateinit var alertLocator: AlertLocator


    @Mock
    internal lateinit var projectService: ProjectService



    internal lateinit var sut: ProjectAlertExecutionService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = ProjectAlertExecutionService(alertLocator, projectService, projectAlertExecutionRepository)
    }

    @Test
    fun test_getShowAlerts_calledWithProjectId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"3", 1, 1)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"3", 1, 0)).thenReturn(Optional.empty())
        sut.getShowAlerts("3")
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"3", 1, 1)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"3", 1, 0)
    }

    @Test
    fun test_getShowAlerts_calledWithNoProjectId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 1)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 0)).thenReturn(Optional.empty())
        sut.getShowAlerts(null)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 1)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 0)
    }

    @Test
    fun test_getShowAlerts_calledWithProjectIdAndReadId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 1, 23)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"23", 1, 0)).thenReturn(Optional.empty())
        sut.getShowAlerts("23", 23)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 1, 23)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"23", 1, 0)
    }


    @Test
    fun test_getShowAlerts_calledWithProjectIdAndReadIdAndUnreadId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 1, 23)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 0, 45)).thenReturn(Optional.empty())
        sut.getShowAlerts("23", 23, 45)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 1, 23)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 0, 45)
    }

    @Test
    fun test_getShowAlerts_calledWithProjectIdAndUnreadId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"23", 1, 1)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 0, 45)).thenReturn(Optional.empty())
        sut.getShowAlerts("23", null, 45)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0,"23", 1, 1)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,"23", 1, 0, 45)
    }


    @Test
    fun test_getShowAlerts_calledWithNoProjectIdAndReadId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 1, 23)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 0)).thenReturn(Optional.empty())
        sut.getShowAlerts(null, 23)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 1, 23)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 0)
    }


    @Test
    fun test_getShowAlerts_calledWithNoProjectIdAndReadIdAndUnreadId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 1, 23)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 0, 45)).thenReturn(Optional.empty())
        sut.getShowAlerts(null, 23, 45)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 1, 23)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 0, 45)
    }

    @Test
    fun test_getShowAlerts_calledWithNoProjectIdAndUnreadId_correctCallToInnerRepository() {
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 1)).thenReturn(Optional.empty())
        Mockito.`when`(projectAlertExecutionRepository.findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 0, 45)).thenReturn(Optional.empty())
        sut.getShowAlerts(null, null, 45)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0,1, 1)
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(0,1, 0, 45)
    }


    @Test(expected = DomainException::class)
    fun test_getTweetsForAlert_calledWithUnexistentAlert_throw() {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndId(0,"22")).thenReturn(Optional.empty())
        sut.getTweetsForAlert("22")
    }

    @Test(expected= DomainException::class)
    fun test_getTweetsForAlert_calledWithIdThatIsNotShowAlert_throw() {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndId(0,"22")).thenReturn(Optional.of(ProjectAlertExecution("22", "1", "alias1", showAlert=0)))
        sut.getTweetsForAlert("22")
    }

    @Test
    fun test_getTweetsForAlert_calledWithExistentShowAlert_correctCallToGetTweets() {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndId(0,"22")).thenReturn(Optional.of(ProjectAlertExecution("22", "1", "alias1", SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").parse("2018-05-04T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").parse("2018-05-07T12:12:12"), showAlert=1)))
        val project = ProjectMother().testInstance(name = "project1", owner = "owner23")
        val alertStrategyMock = Mockito.mock(AlertStrategy::class.java)
        Mockito.`when`(projectService.getById("1")).thenReturn(project)
        Mockito.`when`(alertLocator.getAlertStrategy("alias1", hashMapOf(), project)).thenReturn(alertStrategyMock)
        sut.getTweetsForAlert("22", "213", 3)
        Mockito.`verify`(alertStrategyMock, times(1)).getTweets("2018-05-04T12:12:12", "2018-05-07T12:12:12", "213", 3)
    }


    @Test
    fun test_deleteProjectExecutionAlerts_idsGivenAndIdsExists_correctCallToInnerDelete() {
        sut.deleteProjectExecutionAlerts(arrayListOf("32", "44"))
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).delete("32")
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).delete("44")
    }


    @Test
    fun test_deleteProjectExecutionAlerts_idsGivenAndOneOfThemDoesntExist_correctCallToInnerDelete() {
        Mockito.`when`(projectAlertExecutionRepository.delete("44")).thenAnswer {
            throw EmptyResultDataAccessException(1)
        }
        sut.deleteProjectExecutionAlerts(arrayListOf("32", "44"))
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).delete("32")
        Mockito.`verify`(projectAlertExecutionRepository, times(1)).delete("44")
    }



    @Test
    fun test_archiveProjectExecutionAlerts_idsGivenAndIdsExists_correctCallToInnerSave() {
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndId(0, "32")).thenReturn(Optional.of(ProjectAlertExecution("32", "1", "alias1", SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").parse("2018-05-04T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").parse("2018-05-07T12:12:12"), showAlert=1)))
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndId(0, "68")).thenReturn(Optional.of(ProjectAlertExecution("68", "1", "alias1", SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").parse("2018-05-04T12:12:12"), SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").parse("2018-05-07T12:12:12"), showAlert=1)))
        Mockito.`when`(projectAlertExecutionRepository.findByArchivedAndId(0,"44")).thenReturn(Optional.empty())
        sut.archiveProjectExecutionAlerts(arrayListOf("32", "44", "68"))
        val argument = ArgumentCaptor.forClass(ProjectAlertExecution::class.java)
        Mockito.`verify`(projectAlertExecutionRepository, times(2)).save(argument.capture())
        Assert.assertEquals(1, argument.value.archived)
    }


    @Test(expected=DomainException::class)
    fun test_deleteProjectExecutionAlerts_calledWithAnotherExceptionOcurred_throw() {
        Mockito.`when`(projectAlertExecutionRepository.delete("44")).thenAnswer {
            throw DomainException("")
        }
        sut.deleteProjectExecutionAlerts(arrayListOf("32", "44"))
    }


}