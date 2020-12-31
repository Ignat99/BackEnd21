package backend21.domain

import backend21.application.user.UserDTO
import backend21.domain.user.User
import org.junit.Assert
import org.junit.Test
import backend21.mother.UserMother

class UserUnitTest {
    @Test
    fun test_fromDTO_called_correctResultDTO() {
        val user = getTestInstance()
        val expected = "testUser"
        Assert.assertEquals(expected, user.username)
    }

    @Test
    fun test_hasSamePassword_calledWithSamePassword_returnTrue() {
        val user = getTestInstance()
        val actual = user.hasSamePassword("apassword")
        val expected = true
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_hasSamePassword_calledWithDistinctPassword_returnFalse() {
        val user = getTestInstance()
        val actual = user.hasSamePassword("anotherpassword")
        val expected = false
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun test_toDTO_called_correctResultDTO() {
        val user = getTestInstance()
        val userDTO = user.toDTO()
        // val expected = UserDTO("testUser", "email@todo.es", "apassword", "luis", "heredia")
        Assert.assertEquals("email@todo.es", userDTO.email)
        Assert.assertEquals("testUser", userDTO.username)
        Assert.assertEquals("", userDTO.password)
        Assert.assertEquals("luis", userDTO.name)
        Assert.assertEquals("heredia", userDTO.surname)
    }

    private fun getTestInstance(): User {
        val userDTO = UserDTO("testUser", "email@todo.es", "apassword", "luis", "heredia")
        val user = User.fromDTO(userDTO)
        return user
    }


}
