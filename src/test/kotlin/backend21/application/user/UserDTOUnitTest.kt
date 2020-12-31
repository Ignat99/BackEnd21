package backend21.application.user

import backend21.domain.DomainException
import org.junit.Assert
import org.junit.Test

class UserDTOUnitTest {
    @Test
    fun test_constructor_withValidEmail_correctInstance() {
        val userDTO = UserDTO("name", "todo@todo.es", "werwer", "luis", "heredia")
        val expected = "name"
        Assert.assertEquals(expected, userDTO.username)
    }


    @Test(expected=DomainException::class)
    fun test_constructor_withInvalidEmail_throw() {
        UserDTO("name", "tpep", "werwer", "luis", "heredia")
    }
}