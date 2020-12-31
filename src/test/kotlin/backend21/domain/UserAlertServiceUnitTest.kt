package backend21.domain

import backend21.application.alert.UserAlertDTOUpdate
import backend21.application.project.ProjectDTOUpdate
import backend21.domain.alert.UserAlert
import backend21.domain.alert.UserAlertService
import backend21.domain.alert.interfaces.UserAlertRepository
import backend21.domain.project.Project
import backend21.domain.user.UserService
import backend21.domain.user.interfaces.UserRepository
import backend21.mother.ProjectMother
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.dao.EmptyResultDataAccessException
import java.text.SimpleDateFormat

class UserAlertServiceUnitTest {
    @Mock
    internal lateinit var userAlertRepository: UserAlertRepository

    internal lateinit var sut: UserAlertService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = UserAlertService(userAlertRepository)
    }


    @Test
    fun test_deleteUserAlert_calledWithUnexistentId_nothingHappens() {
        Mockito.`when`(userAlertRepository.delete("32")).thenAnswer( {
            throw EmptyResultDataAccessException(2)
        })
        sut.deleteUserAlert("32")
        Assert.assertTrue(true)
    }


    @Test
    fun test_updateUserAlert_called_correctCallToInnerSave() {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")
        val sourceUserAlert = UserAlert("23", "22", "12", "alert_username", hashMapOf("username" to "juan", "userMention" to "luis"), dateFormat.parse("2016/08/07T11:29:12"), type="user")
        val updateUserAlert = UserAlertDTOUpdate(0, 0)
        sut.updateUserAlert(sourceUserAlert, updateUserAlert)
        val argument = ArgumentCaptor.forClass(UserAlert::class.java)
        Mockito.verify(userAlertRepository, Mockito.times(1)).save(argument.capture())
        Assert.assertEquals(0, argument.value.active)
        Assert.assertEquals(0, argument.value.email)
    }

}