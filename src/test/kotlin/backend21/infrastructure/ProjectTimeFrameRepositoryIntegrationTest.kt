package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.project.ProjectTimeFrame
import backend21.domain.project.interfaces.ProjectTimeFrameRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.text.SimpleDateFormat

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [(SpringEntryPoint::class)])
@TestPropertySource("classpath:test.properties")
class ProjectTimeFrameRepositoryIntegrationTest {
    @Autowired
    private var projectTimeFrameRepository: ProjectTimeFrameRepository?=null
    private var projectTimeFrame: ProjectTimeFrame? = null

    @Before
    fun setUp() {
        projectTimeFrameRepository?.deleteAll()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        projectTimeFrame = ProjectTimeFrame("23", dateFormat.parse("2014/06/06 12:12:00"), dateFormat.parse("2016/08/07 11:29:12"), 0)
        setupInitialProject()
    }

    @After
    fun tearDown() {
        projectTimeFrameRepository?.deleteAll()
    }

    @Test
    fun test_save_saveUnexistentProject_correctInsertion() {
        val actual = projectTimeFrameRepository?.findOne("23")
        val expected = "23"
        Assert.assertEquals(expected, actual?.id)
    }

    @Test
    fun test_save_existsProjectWithSameId_updateProject() {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val updatedProjectTimeframe = ProjectTimeFrame("23", dateFormat.parse("2014/07/05 11:11:00"), dateFormat.parse("2016/08/07 11:29:12"), 1)
        projectTimeFrameRepository?.save(updatedProjectTimeframe)
        val persistedProjectsTimeFrame = projectTimeFrameRepository?.findAll()
        val expected = 1
        Assert.assertEquals(1, persistedProjectsTimeFrame?.size)
        Assert.assertEquals(expected, persistedProjectsTimeFrame!![0]?.realtime)
    }


    @Test
    fun test_findById_existTimeFrameWithId_returnCorrectTimeFrame() {
        val projectTimeFrame = projectTimeFrameRepository?.findById("23")
        Assert.assertEquals(0, projectTimeFrame?.get()?.realtime)
    }


    @Test
    fun test_findById_notExistsTimeFrameWithId_returnEmpty() {
        val projectTimeFrame = projectTimeFrameRepository?.findById("233")
        Assert.assertEquals(false, projectTimeFrame?.isPresent)
    }

    private fun setupInitialProject() {
        val retrievedProject = projectTimeFrameRepository?.findOne("23")
        Assert.assertNull("guard - project exists", retrievedProject)
        projectTimeFrameRepository?.save(projectTimeFrame)
    }

}