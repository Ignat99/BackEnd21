package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.project.Project
import backend21.domain.project.interfaces.ProjectRepository
import backend21.mother.ProjectMother
import org.h2.jdbc.JdbcSQLException
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.*

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [(SpringEntryPoint::class)])
@TestPropertySource("classpath:test.properties")
class ProjectRepositoryIntegrationTest {

    @Autowired
    private var projectRepository: ProjectRepository?=null
    private var project: Project? = null

    @Before
    fun setUp() {
        projectRepository?.deleteAll()
        project = ProjectMother().testInstance("1", "project1", "owner")
        setupInitialProject()
    }

    @After
    fun tearDown() {
        projectRepository?.deleteAll()
    }

    @Test
    fun test_save_saveUnexistentProject_correctInsertion() {
        val actual = projectRepository?.findOne("1")
        val expected = "project1"
        Assert.assertEquals(expected, actual?.name)
    }

    @Test
    fun test_save_existsProjectWithSameId_updateUser() {
        val updatedProject = Project(project?.id, "another name", "keyword", project?.getKeywords(), project?.getExcludedKeywords(), project?.getSources(), project?.getTeamMembers(), project?.description)
        projectRepository?.save(updatedProject)
        val persistedProject = projectRepository?.findAll()
        val expected = "another name"
        Assert.assertEquals(1, persistedProject?.size)
        Assert.assertEquals(expected, persistedProject!![0]?.name)
        Assert.assertEquals(1, persistedProject[0]?.active)
    }

    @Test(expected = DataIntegrityViolationException::class)
    fun test_save_calledWithMoreThan2000CharsInKeywords_throw() {
        val createdProject = Project("4", "testName", "keyword", arrayListOf("Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicer", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicer", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicer", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicer"))
        projectRepository?.save(createdProject)
    }


    @Test
    fun test_findAllKeywords_calledWithExistentKeywords_returnAListWithAllKeywords() {
        val project1 = Project("3", "anotherName", "keyword", arrayListOf("ala", "lele"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
        projectRepository?.save(project1)
        val project2 = Project("4", "project4", "keyword", arrayListOf("mula omar"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
        project2.active = 0
        projectRepository?.save(project2)
        val keywords = projectRepository?.findAllKeywords()
        val expected = arrayListOf<String>("ala,alo man", "ala,lele")
        Assert.assertEquals(expected, keywords)
    }


    @Test
    fun test_findByOwner_calledWithNoProjects_returnEmptyList() {
        val result: Optional<List<Project>> = projectRepository?.findByOwnerAndActive("owner2", 1)!!
        Assert.assertEquals(false, result.isPresent)
    }



    @Test
    fun test_findByOwner_calledWithProjects_returnCorrectProjects() {
        setUpProjects()
        val result: Optional<List<Project>> = projectRepository?.findByOwnerAndActive("owner2", 1)!!
        Assert.assertEquals(2, result.get().size)

        val actual = result.get().map { it.id }
        Assert.assertEquals(arrayListOf("2", "4"), actual)
    }


    @Test
    fun test_findByIdAndOwnerAndActive_calledWithProjectIdThatExistsAndOwnerThatMatch_returnCorrectProject() {
        setUpProjects()
        val result: Optional<Project> = projectRepository!!.findByIdAndOwnerAndActive("4", "owner2", 1)
        val actual = result.get()
        Assert.assertEquals("project4", actual.name)
    }

    @Test
    fun test_findByIdAndOwnerAndActive_calledWithProjectIdThatExistsAndOwnerThatDoesntMatch_returnEmptyProject() {
        setUpProjects()
        val result: Optional<Project> = projectRepository!!.findByIdAndOwnerAndActive("4", "owner3", 1)
        Assert.assertEquals(false, result.isPresent)
    }


    @Test
    fun test_findByIdAndOwnerAndActive_calledWithProjectIdThatExistsAndOwnerThatMatchAndNotActive_returnEmptyProject() {
        setUpProjects()
        val result: Optional<Project> = projectRepository!!.findByIdAndOwnerAndActive("4", "owner2", 0)
        Assert.assertEquals(false, result.isPresent)
    }

    @Test
    fun test_findByIdAndOwnerAndActive_calledWithProjectIdThatNotExistsAndOwnerThatMatch_returnCorrectProject() {
        setUpProjects()
        val result: Optional<Project> = projectRepository!!.findByIdAndOwnerAndActive("44", "owner2", 1)
        Assert.assertEquals(false, result.isPresent)
    }


    @Test
    fun test_findByOwnerAndActiveWithTerm_calledWithProjects_returnCorrectProjects() {
        projectRepository?.save(ProjectMother().testInstance("2", "project term1", "owner2"))
        projectRepository?.save(ProjectMother().testInstance("3", "project term2", "owner2", "me gusta term1 que mola"))
        projectRepository?.save(ProjectMother().testInstance("4", "project term1", "owner2"))
        projectRepository?.save(ProjectMother().testInstance("5", "project term3", "owner2", "me dices algo de Term1 o que"))
        projectRepository?.save(ProjectMother().testInstance("6", "project term2", "owner2"))
        projectRepository?.save(ProjectMother().testInstance("7", "project term1", "owner3"))

        projectRepository?.flush()
        val result: Optional<List<Project>> = projectRepository!!.findByOwnerAndActiveWithTerm("owner2", 1, "term1")
        val expected = "2|3|4|5|"
        verifyProjectIds(result, expected)
    }

    private fun verifyProjectIds(result: Optional<List<Project>>, expected: String) {
        var actual: String = ""
        result.get().map { actual += it.id + "|" }
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun test_findByActive_called_returnCorrectProjects() {
        setUpProjects()
        val result: Optional<List<Project>> = projectRepository!!.findByActive(1)
        verifyProjectIds(result, "1|2|3|4|")
    }



    private fun setUpProjects() {
        projectRepository?.save(ProjectMother().testInstance("2", "project2", "owner2"))
        projectRepository?.save(ProjectMother().testInstance("3", "project3", "owner3"))
        projectRepository?.save(ProjectMother().testInstance("4", "project4", "owner2"))
        val testInstanceNoActive1 = ProjectMother().testInstance("5", "project5", "owner2")
        testInstanceNoActive1.active = 0
        projectRepository?.save(testInstanceNoActive1)
        val testInstanceNoActive2 = ProjectMother().testInstance("6", "project6", "owner2")
        testInstanceNoActive2.active = 0
        projectRepository?.save(testInstanceNoActive2)
        projectRepository?.flush()
    }





    private fun setupInitialProject() {
        val retrievedProject = projectRepository?.findOne("1")
        Assert.assertNull("guard - project exists", retrievedProject)
        projectRepository?.save(project)
    }


}