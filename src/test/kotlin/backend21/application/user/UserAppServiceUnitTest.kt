package backend21.application.user

import backend21.application.ApplicationServiceException
import backend21.domain.DomainException
import backend21.domain.user.UserService
import backend21.mother.UserMother
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class UserAppServiceUnitTest {
    internal lateinit var sut: UserAppService

    @Mock
    internal lateinit var userService: UserService



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = UserAppService(userService)
    }

    @Test(expected= ApplicationServiceException::class)
    fun test_createUser_userExists_throw() {
        Mockito.`when`(userService.findByUsername("admi")).thenReturn(UserMother().testAdminInstance())
        sut.createUser(UserMother().testAdminInstance().toDTO())
    }

    @Test
    fun test_createUser_userNotExist_correctCallToInnerUserService() {
        Mockito.`when`(userService.findByUsername("admi")).thenAnswer( {
            throw DomainException("")
        })
        Mockito.`when`(userService.createUser(any())).thenReturn(UserMother().testAdminInstance())
        sut.createUser(UserMother().testAdminInstance().toDTO())
        Mockito.verify(userService, times(1)).createUser(any())
    }





    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T
}