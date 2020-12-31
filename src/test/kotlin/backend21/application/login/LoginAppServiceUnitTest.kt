package backend21.application.login

import backend21.application.ApplicationServiceException
import backend21.domain.DomainException
import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.oauthcredentials.OAuthCredentials
import backend21.domain.oauthcredentials.OAuthCredentialsService
import backend21.domain.user.User
import backend21.domain.user.UserService
import backend21.domain.user.interfaces.UserRepository
import backend21.mother.UserMother
import backend21.security.SecurityUserDetails
import backend21.services.UserDetailsService
import org.apache.http.auth.InvalidCredentialsException
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

class LoginAppServiceUnitTest {
    internal lateinit var sut: LoginAppService

    @Mock
    internal lateinit var userDetailsService: UserDetailsService

    @Mock
    internal lateinit var oauth2Service: OAuthCredentialsService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = LoginAppService(userDetailsService, oauth2Service)
    }

    @Test(expected=UsernameNotFoundException::class)
    fun test_login_userNotExists_throw() {
        Mockito.`when`(userDetailsService.loadUserByUsername("testUsername")).thenAnswer( {
            throw UsernameNotFoundException("")
        })
        sut.login(LoginDTO("testUsername", "testPassword"))
    }

    @Test(expected = ApplicationServiceException::class)
    fun test_login_userExistsWithIncorrectPassword_throw() {
        Mockito.`when`(userDetailsService.loadUserByUsername("testUsername")).thenReturn(SecurityUserDetails(User.fromDTO(UserMother().testAdminInstance().toDTO())))
        sut.login(LoginDTO("testUsername", "dummyPassword"))
    }

    @Test
    fun test_login_userExistsWithCorrectPassword_returnCorrectCredentials() {
        val user = UserMother().testAdminInstance()
        Mockito.`when`(userDetailsService.loadUserByUsername("admi")).thenReturn(SecurityUserDetails(user))
        Mockito.`when`(oauth2Service.getAccessToken("admi", user.password!!)).thenReturn(OAuthCredentials("access", "Bearer", "refresh", 234234, "read"))
        val actual = sut.login(LoginDTO("admi", "asfdafsaf"))
        val expected = "OAuthCredentialsDTO(access_token=access, tokenType=Bearer, refreshToken=refresh, expiresIn=234234, scope=read)"
        Assert.assertEquals(expected, actual.login.toString())
    }

    @Test(expected=CredentialsException::class)
    fun test_login_userExistsWithCorrectPasswordButGetAccessTokenThrowsException_throw() {
        val user = UserMother().testAdminInstance()
        Mockito.`when`(userDetailsService.loadUserByUsername("admi")).thenReturn(SecurityUserDetails(user))
        Mockito.`when`(oauth2Service.getAccessToken("admi", user.password!!)).thenAnswer({throw CredentialsException("") })
        sut.login(LoginDTO("admi", "asfdafsaf"))
    }


}



