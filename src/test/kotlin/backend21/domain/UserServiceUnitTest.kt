package backend21.domain

import backend21.domain.user.User
import backend21.domain.user.UserService
import backend21.domain.user.interfaces.UserRepository
import backend21.mother.UserMother
import backend21.application.user.UserDTO
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class UserServiceUnitTest {
    internal lateinit var sut: UserService
//    internal lateinit var user0: User

    @Mock
    internal lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = UserService(userRepository)
//        user0 = UserMother().testAdminInstance()
    }

//    @Test
//    fun test_createUser_returnUser() {
////        Mockito.`when`(sut.createUser(user0)).thenReturn(UserMother().testAdminInstance())
//        val user0 = User.fromDTO(UserDTO("adam","adam@todo.es","123","adam","adam"))
//        val user1 = sut.createUser(user0)
////        val user2 = sut.findByUsername("adam")
////        Assert.assertEquals("adam", user2.username)
//    }


    @Test
    fun test_findByUsername_userExists_returnUser() {
        Mockito.`when`(userRepository.findByUsername("testUsername")).thenReturn(Optional.of(UserMother().testAdminInstance()))
        val user = sut.findByUsername("testUsername")
        Assert.assertEquals("admi", user.username)
    }

    @Test(expected=DomainException::class)
    fun test_findByUsername_userNotExists_throw() {
        Mockito.`when`(userRepository.findByUsername("testUsername")).thenReturn(Optional.empty())
        sut.findByUsername("testUsername")
    }

    @Test
    fun test_findById_userExist_returnUser() {
        Mockito.`when`(userRepository.findById("1")).thenReturn(Optional.of(UserMother().testAdminInstance()))
        val user = sut.findById("1")
        Assert.assertEquals("admi", user.username)
    }

    @Test(expected=DomainException::class)
    fun test_findById_userNotExists_throw() {
        Mockito.`when`(userRepository.findById("100")).thenReturn(Optional.empty())
        sut.findById("100")
    }


}