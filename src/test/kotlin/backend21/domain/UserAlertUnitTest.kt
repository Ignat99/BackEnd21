package backend21.domain

import backend21.domain.alert.UserAlert
import org.junit.Assert
import org.junit.Test
import java.util.*

class UserAlertUnitTest {
    @Test
    fun test_constructor_createdWithEmptyExtravar_correctInstance() {
        val sut = UserAlert("32", "1", "2", "user_mention", type="user")
        val extravars = sut.getExtravars()
        Assert.assertEquals(hashMapOf<String, String>(), extravars)
    }

    @Test
    fun test_constructor_createdWithExtravar_correctInstance() {
        val expected = hashMapOf("username" to "luis")
        val sut = UserAlert("32", "1", "2", "user_mention", expected, type="user")
        val actual = sut.getExtravars()
        Assert.assertEquals(expected, actual)
    }

    @Test(expected=DomainException::class)
    fun test_constructor_invalidType_throw() {
        UserAlert("32", "1", "2", "user_mention", null, Date(), "wawa")
    }
}