package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.flag.Flag
import backend21.domain.flag.interfaces.FlagRepository
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

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [(SpringEntryPoint::class)])
@TestPropertySource("classpath:test.properties")
class FlagRepositoryIntegrationTest {
    @Autowired
    private lateinit var flagRepository: FlagRepository
    private var flag: Flag? = null

    @Before
    fun setUp() {
        flagRepository.deleteAll()
        flag = Flag("23", "a name", "3")
        setupInitialProject()
    }

    @After
    fun tearDown() {
        flagRepository.deleteAll()
    }

    @Test
    fun test_save_saveUnexistentFlag_correctInsertion() {
        val actual = flagRepository.findOne("23")
        val expected = "a name"
        Assert.assertEquals(expected, actual?.name)
    }

    @Test(expected= DataIntegrityViolationException::class)
    fun test_save_existsFlagWithSameName_throw() {
        flagRepository.save(Flag("24", "a name"))
    }


    @Test(expected= DataIntegrityViolationException::class)
    fun test_save_existsFlagWithSameNameInsensitive_throw() {
        flagRepository.save(Flag("24", "A Name"))
    }

    @Test
    fun test_save_saveUnexistentFlag_insertWithActiveFlagEnabled() {
        val actual = flagRepository.findOne("23")
        val expected = 1
        Assert.assertEquals(expected, actual?.active)
    }


    @Test
    fun test_findByProjectId_calledWithUnexistentProject_returnEmptyList() {
        flagRepository.save(Flag("3", "name3", "2"))
        val result = flagRepository.findByProjectId("44")
        Assert.assertEquals(false, result.isPresent)
    }


    @Test
    fun test_findByProjectId_calledWithExistentProject_returnCorrectList() {
        flagRepository.save(Flag("1", "name1", "2"))
        flagRepository.save(Flag("2", "name2", "4"))
        flagRepository.save(Flag("3", "name3", "2"))
        flagRepository.save(Flag("4", "name4", "4"))
        flagRepository.save(Flag("5", "name5", "2"))
        flagRepository.save(Flag("6", "name6", "2"))
        val result = flagRepository.findByProjectId("2")
        val actual = result.get().map { e->e.name   }
        val expected = arrayListOf("name1", "name3", "name5", "name6")
        Assert.assertEquals(expected, actual)
    }




    private fun setupInitialProject() {
        val retrievedFlag = flagRepository.findOne("23")
        Assert.assertNull("guard - flag exists", retrievedFlag)
        flagRepository.save(flag)
    }



}