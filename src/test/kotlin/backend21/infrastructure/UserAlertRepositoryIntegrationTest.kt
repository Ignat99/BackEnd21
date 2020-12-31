package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.alert.UserAlert
import backend21.domain.alert.interfaces.UserAlertRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.text.SimpleDateFormat

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [(SpringEntryPoint::class)])
@TestPropertySource("classpath:test.properties")
class UserAlertRepositoryIntegrationTest {
    @Autowired
    private var userAlertRepository: UserAlertRepository?=null
    private var userAlert: UserAlert? = null

    @Before
    fun setUp() {
        userAlertRepository?.deleteAll()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")
        userAlert = UserAlert("23", "22", "12", "alert_username", hashMapOf("username" to "juan", "userMention" to "luis"), dateFormat.parse("2016/08/07T11:29:12"), type="user")
        setupInitialProject()
    }

    @After
    fun tearDown() {
        userAlertRepository?.deleteAll()
    }

    @Test
    fun test_save_saveUnexistentUserAlert_correctInsertion() {
        val actual = userAlertRepository?.findOne("23")
        val expected = "23"
        Assert.assertEquals(expected, actual?.id)
    }

    @Test
    fun test_save_existsUserAlertWithSameId_updateUserAlert() {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")
        val updatedUserAlert = UserAlert("23", "33", "11", "user_2", hashMapOf(), dateFormat.parse("2014/07/05T11:11:00"), "user")
        userAlertRepository?.save(updatedUserAlert)
        val persistedUserAlert = userAlertRepository?.findAll()
        val expected = 1
        Assert.assertEquals(expected, persistedUserAlert?.size)
        Assert.assertEquals("user_2", persistedUserAlert!![0]?.alias)
    }


    @Test
    fun test_findById_existTimeFrameWithId_returnCorrectTimeFrame() {
        val userAlert = userAlertRepository?.findById("23")
        Assert.assertEquals("alert_username", userAlert?.get()?.alias)
    }


    @Test
    fun test_findById_notExistsTimeFrameWithId_returnEmpty() {
        val userAlert = userAlertRepository?.findById("233")
        Assert.assertEquals(false, userAlert?.isPresent)
    }


    @Test
    fun test_findByUserId_calledWithExistentUserInAlerts_returnListUserAlerts() {
        setUpUserAlerts()
        val userAlerts = userAlertRepository?.findByUserId("12")
        val expected = arrayListOf("23","25", "27")
        val actual = userAlerts?.get()?.map { it.id }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_findByProjectId_calledWithExistentProjectInAlerts_returnListUserAlerts() {
        setUpUserAlerts()
        val userAlerts = userAlertRepository?.findByProjectId("23")
        val expected = arrayListOf("25", "27")
        val actual = userAlerts?.get()?.map { it.id }
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun test_findByProjectId_calledWithNoExistentProject_returnEmptyList() {
        val userAlerts = userAlertRepository?.findByProjectId("225")
        val expected = false
        Assert.assertEquals(expected, userAlerts?.isPresent)
    }


    @Test
    fun test_findByUserId_calledWithNoExistentUser_returnEmptyList() {
        val userAlerts = userAlertRepository?.findByUserId("225")
        val expected = false
        Assert.assertEquals(expected, userAlerts?.isPresent)
    }

    @Test
    fun test_findByProjectIdAndActive_called_returnCorrectList() {
        setUpUserAlerts()
        val userAlerts = userAlertRepository?.findByProjectIdAndActive("23", 1)
        var actual = ""
        userAlerts?.get()!!.map { actual += it.id + "|" }
        val expected = "27|"
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun test_delete_calledWithExistentId_correctDeletion() {
        setupAndGuardDeleteTest()
        userAlertRepository?.delete("26")
        userAlertRepository?.delete("24")
        val expected = "23|25|27|"
        doDeleteTest(expected)
    }


    @Test(expected= EmptyResultDataAccessException::class)
    fun test_delete_calledWithUnexistentId_throw() {
        setupAndGuardDeleteTest()
        userAlertRepository?.delete("216")

    }


    private fun doDeleteTest(expected: String) {
        val alerts = userAlertRepository?.findAll()
        var actual = ""
        alerts?.map { actual += it.id + "|" }
        Assert.assertEquals(expected, actual)
    }

    private fun setupAndGuardDeleteTest() {
        setUpUserAlerts()
        val userAlerts = userAlertRepository?.findAll()
        Assert.assertEquals("guard - Incorrect initial number of alerts", 5, userAlerts?.size)
    }


    private fun setupInitialProject() {
        val retrievedProject = userAlertRepository?.findOne("23")
        Assert.assertNull("guard - userAlert exists", retrievedProject)
        userAlertRepository?.save(userAlert)
    }

    private fun setUpUserAlerts() {
        val userAlert1 = UserAlert("24", "22", "11", "alert_username", hashMapOf("username" to "juan", "userMention" to "luis"), type = "user")
        val userAlert2 = UserAlert("25", "23", "12", "alert_username2", hashMapOf("username" to "juan", "userMention" to "luis"), type = "user", active=0)
        val userAlert3 = UserAlert("26", "22", "11", "alert_username3", hashMapOf("username" to "juan", "userMention" to "luis"), type = "user")
        val userAlert4 = UserAlert("27", "23", "12", "alert_username4", hashMapOf("username" to "juan", "userMention" to "luis"), type = "user", active=1)
        userAlertRepository?.save(listOf(userAlert1, userAlert2, userAlert3, userAlert4))
    }

}