package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
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
class ProjectAlertExecutionIntegrationTest {
    @Autowired
    private var projectAlertExecutionRepository: ProjectAlertExecutionRepository? = null
    private var projectAlertExecution: ProjectAlertExecution? = null

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    @Before
    fun setUp() {
        projectAlertExecutionRepository?.deleteAll()
        projectAlertExecution = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2016-03-03T12:12:12"), simpleDateFormat.parse("2016-04-03T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2016-04-03T12:12:12"))
        setupInitialProjectAlertExecution()
    }

    @After
    fun tearDown() {
        projectAlertExecutionRepository?.deleteAll()
    }


    @Test
    fun test_save_saveUnexistentProjectAlertExecution_correctInsertion() {
        val actual = projectAlertExecutionRepository?.findAll()
        val expected = "num_posts"
        Assert.assertEquals(1, actual?.size)
        Assert.assertEquals(expected, actual?.first()?.alias)
    }

    @Test
    fun test_save_existsProjectAlertExecutionWithSameId_updateUser() {
        val updatedProjectAlertExecution = ProjectAlertExecution(projectAlertExecution?.id, projectAlertExecution?.projectId, "numero_posts", simpleDateFormat.parse("2016-03-03T12:12:12"), simpleDateFormat.parse("2016-04-03T12:12:12"), "num=100000", 1, projectAlertExecution?.executedAt)
        projectAlertExecutionRepository?.save(updatedProjectAlertExecution)
        val persistedProjectAlertExecution = projectAlertExecutionRepository?.findAll()
        val expected = "numero_posts"
        Assert.assertEquals(1, persistedProjectAlertExecution?.size)
        Assert.assertEquals(expected, persistedProjectAlertExecution!![0]?.alias)
        Assert.assertEquals("num=100000", persistedProjectAlertExecution[0]?.extravars)
    }


    @Test
    fun test_findByAliasAndProjectIdAndExtravarsOrderByExecutedAtDesc_called_returnCorrectProjectExecutionAlerts() {
        setUpProjectAlertExecution()
        val projectAlertExecutions = projectAlertExecutionRepository?.findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(0, "num_posts", "22", "num=100000")
        Assert.assertEquals(3, projectAlertExecutions?.get()?.count())

    }


    @Test
    fun test_findTop20ByShowAlertAndReadOrderByIdDesc_called_returnCorrectProjectExecutionAlerts() {
        val read = 1
        val expected = 3
        doFindByShowAlertAndReadTest(read, expected)
    }


    @Test
    fun test_findTop20ByShowAlertAndReadOrderByIdDesc_calledWithUnread_returnCorrectProjectExecutionAlerts() {
        val read = 0
        val expected = 5
        doFindByShowAlertAndReadTest(read, expected)
    }


    @Test
    fun test_findTop20ByProjectIdAndShowAlertAndReadOrderByIdDesc_calledWithUnread_returnCorrectProjectExecutionAlerts() {
        setUpProjectAlert()
        val results = projectAlertExecutionRepository?.findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(0, "23", 1, 1)
        Assert.assertEquals(1, results?.get()?.size)
    }

    @Test
    fun test_findById_idExists_returnCorrectProjectAlertExecution() {
        setUpProjectAlert()
        val projectAlertExecutions = projectAlertExecutionRepository?.findAll()
        val projectAlertExecution = projectAlertExecutions?.first()
        val actual = projectAlertExecutionRepository?.findByArchivedAndId(0, projectAlertExecution?.id!!)
        Assert.assertEquals(projectAlertExecution, actual?.get())
    }


    @Test
    fun test_delete_idExists_correctDeletion() {
        setUpProjectAlert()
        val projectAlertExecutions = projectAlertExecutionRepository?.findAll()
        val how = projectAlertExecutions?.size

        val id2 = projectAlertExecutions?.last()?.id
        val exist = projectAlertExecutionRepository?.findByArchivedAndId(0, id2!!)
        Assert.assertEquals("guard - project alert doesn't exist", true, exist?.isPresent)
        projectAlertExecutionRepository?.delete(id2)
        val actual = projectAlertExecutionRepository?.findByArchivedAndId(0, id2!!)
        Assert.assertEquals(false, actual?.isPresent)
    }


    private fun doFindByShowAlertAndReadTest(read: Int, expected: Int) {
        setUpProjectAlert()
        val results = projectAlertExecutionRepository?.findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(0, 1, read)
        Assert.assertEquals(expected, results?.get()?.size)
    }

    private fun setUpProjectAlert() {
        setUpProjectAlertExecution()
        val pae1 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), 1)
        val pae2 = ProjectAlertExecution(null, "22", "hashtag_in", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), 1)
        projectAlertExecutionRepository?.save(listOf(pae1, pae2))
        projectAlertExecutionRepository?.flush()
    }


    private fun setUpProjectAlertExecution() {
        val pae1 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
        val pae2 = ProjectAlertExecution(null, "22", "hashtag_in", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
        val pae3 = ProjectAlertExecution(null, "23", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-23T12:12:12"), 1)
        val pae4 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000,ratio=0.1", 1, simpleDateFormat.parse("2018-04-24T12:12:12"))
        val pae5 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=300000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"))
        val pae6 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), archived = 1)
        val pae7 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), archived = 1)
        val pae8 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), archived = 1)
        val pae9 = ProjectAlertExecution(null, "22", "num_posts", simpleDateFormat.parse("2018-03-20T12:12:12"), simpleDateFormat.parse("2018-04-20T12:12:12"), "num=100000", 1, simpleDateFormat.parse("2018-04-20T12:12:12"), archived = 1)
        projectAlertExecutionRepository?.save(listOf(pae1, pae2, pae3, pae4, pae5, pae6, pae7, pae8, pae9))
        projectAlertExecutionRepository?.flush()
    }


    private fun setupInitialProjectAlertExecution() {
        val retrievedProjectAlertExecution = projectAlertExecutionRepository?.findOne("1")
        Assert.assertNull("guard - project alert execution exists", retrievedProjectAlertExecution)
        projectAlertExecutionRepository?.save(projectAlertExecution)
    }
}