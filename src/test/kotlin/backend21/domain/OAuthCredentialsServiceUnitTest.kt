package backend21.domain

import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.oauthcredentials.OAuthCredentials
import backend21.domain.oauthcredentials.OAuthCredentialsService
import backend21.mother.UserMother
import backend21.wrappers.CurlWrapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class OAuthCredentialsServiceUnitTest {
    internal lateinit var sut: OAuthCredentialsService

    @Mock
    internal lateinit var curlWrapper: CurlWrapper


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = OAuthCredentialsService(curlWrapper)
    }

    @Test(expected = CredentialsException::class)
    fun test_getAccessToken_calledWithErrorInWrapper_throw() {
        configureErrorCurlWrapperStub()
        sut.getAccessToken("testUser", "testPaasword")
    }

    private fun configureErrorCurlWrapperStub() {
        val result = "{\"error\": \"an error\", \"error_description\": \"error description\"}"
        Mockito.`when`(curlWrapper.doRequest(any())).thenReturn(result)
    }

    @Test
    fun test_getAccessToken_calledWithValidResult_returnCorrectCredentials() {
        configureCurlWrapperStub()
        val actual = sut.getAccessToken("testUser", "testPassword")
        val expected = "OAuthCredentials(access_token=f33ac0d3-1889-4208-b69f-6fc3662d2cb0, tokenType=bearer, refreshToken=c352ec19-6aa0-4a32-bb8b-e0eb6ce57af0, expiresIn=29657, scope=read write)"
        Assert.assertEquals(expected, actual.toString())
    }

    @Test
    fun test_getAccessTokenByRefreshToken_calledWithValidResult_returnCorrectOAuthCredentials() {
        configureCurlWrapperStub()
        val actual = sut.getAccessTokenByRefreshToken("c352ec19-6aa0-4a32-bb8b-e0eb6ce57af3")
        val expected = "OAuthCredentials(access_token=f33ac0d3-1889-4208-b69f-6fc3662d2cb0, tokenType=bearer, refreshToken=c352ec19-6aa0-4a32-bb8b-e0eb6ce57af0, expiresIn=29657, scope=read write)"
        Assert.assertEquals(expected, actual.toString())
    }

    @Test(expected = CredentialsException::class)
    fun test_getAccessTokenByRefreshToken_calledWithErrorInWrapper_throw() {
        configureErrorCurlWrapperStub()
        sut.getAccessTokenByRefreshToken("c352ec19-6aa0-4a32-bb8b-e0eb6ce57af3")
    }




    private fun configureCurlWrapperStub() {
        val result = "{\"access_token\":\"f33ac0d3-1889-4208-b69f-6fc3662d2cb0\",\"token_type\":\"bearer\",\"refresh_token\":\"c352ec19-6aa0-4a32-bb8b-e0eb6ce57af0\",\"expires_in\":29657,\"scope\":\"read write\"}"
        Mockito.`when`(curlWrapper.doRequest(any())).thenReturn(result)
    }


    //This solves a kotlin-mockito issue with any()
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T

}
