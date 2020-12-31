package backend21.domain

import backend21.application.project.ProjectDTOUpdate
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import backend21.domain.project.interfaces.ProjectRepository
import backend21.mother.ProjectMother
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


class ProjectServiceUnitTest {
    internal lateinit var sut: ProjectService

    @Mock
    internal lateinit var projectRepository: ProjectRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = ProjectService(projectRepository)
    }

    @Test
    fun test_getAllKeywords_calledWithKeywords_returnCorrectKeywordsWithoutDuplicates() {
        Mockito.`when`(projectRepository.findAllKeywords()).thenReturn(arrayListOf("a1,a2", "a2,a3", "a1,a4"))
        val actual: List<String> = sut.getAllKeywords()
        val expected = arrayListOf<String>("a1", "a2", "a3", "a4")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_getProjectsByOwner_calledWithTerm_correctCallToInnerRepository() {
        Mockito.`when`(projectRepository.findByOwnerAndActiveWithTerm("owner", 1, "casa")).thenReturn(Optional.empty())
        sut.getProjectsByOwner("owner", "cAsa")
        Mockito.verify(projectRepository, Mockito.times(1)).findByOwnerAndActiveWithTerm("owner", 1, "casa")
    }


    @Test
    fun test_getProjectsByOwner_calledWithNoTerm_correctCallToInnerRepository() {
        Mockito.`when`(projectRepository.findByOwnerAndActive("owner", 1)).thenReturn(Optional.empty())
        sut.getProjectsByOwner("owner")
        Mockito.verify(projectRepository, Mockito.times(1)).findByOwnerAndActive("owner", 1)
    }

    @Test
    fun test_getAllKeywords_calledWithEmptyKeywords_returnEmpryKeywords() {
        Mockito.`when`(projectRepository.findAllKeywords()).thenReturn(arrayListOf())
        val actual: List<String> = sut.getAllKeywords()
        val expected = arrayListOf<String>()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_getById_called_returnSameAsInnerRepository() {
        val testInstance = ProjectMother().testInstance("2", "project1", "owner1")
        Mockito.`when`(projectRepository.findByIdAndActive("2", 1)).thenReturn(Optional.of(testInstance))
        val actual = sut.getById("2")
        val expected = "project1"
        Assert.assertEquals(expected, actual.name)
    }


    @Test(expected = DomainException::class)
    fun test_getById_calledWithNoActiveProjects_throw() {
        Mockito.`when`(projectRepository.findByIdAndActive("2", 1)).thenReturn(Optional.empty())
        sut.getById("2")
    }


    @Test
    fun test_getByIdAndOwner_called_returnSameAsInnerRepositoryIfProjectExists() {
        val testInstance = ProjectMother().testInstance("2", "project1", "owner1")
        Mockito.`when`(projectRepository.findByIdAndOwnerAndActive("2", "owner1", 1)).thenReturn(Optional.of(testInstance))
        val actual = sut.getByIdAndOwner("2", "owner1")
        val expected = "project1"
        Assert.assertEquals(expected, actual.name)
    }


    @Test(expected = DomainException::class)
    fun test_getById_calledWithNoProjects_throw() {
        Mockito.`when`(projectRepository.findByIdAndOwnerAndActive("2", "owner1", 1)).thenReturn(Optional.empty())
        sut.getByIdAndOwner("2", "owner1")
    }


    @Test
    fun test_updateProject_called_correctCallToInnerSave() {
        val sourceProject = ProjectMother().testInstance("1", "project2", "owner2")
        val updateProject = ProjectDTOUpdate(name="project22", description="newDescription", excludedKeywords = arrayListOf("newList", "newList2"))
        sut.updateProject(sourceProject, updateProject)
        val argument = ArgumentCaptor.forClass(Project::class.java)
        Mockito.verify(projectRepository, Mockito.times(1)).save(argument.capture())
        Assert.assertEquals("project22", argument.getValue().name)
        Assert.assertEquals("owner2", argument.getValue().owner)
        Assert.assertEquals("newDescription", argument.getValue().description)
        Assert.assertEquals(arrayListOf("ala", "alo man"), argument.getValue().getKeywords())
        Assert.assertEquals(arrayListOf("newList", "newList2"), argument.getValue().getExcludedKeywords())
    }


    @Test
    fun test_deleteProject_called_correctCallToInnerRepository() {
        val testInstance = ProjectMother().testInstance("1", "project1", "owner1")
        val active = testInstance.active
        Assert.assertEquals("guard- project is not active", 1, active)
        sut.deleteProject(testInstance)
        val argument = ArgumentCaptor.forClass(Project::class.java)
        Mockito.verify(projectRepository, Mockito.times(1)).save(argument.capture())
        Assert.assertEquals(0, argument.getValue().active)
    }






}