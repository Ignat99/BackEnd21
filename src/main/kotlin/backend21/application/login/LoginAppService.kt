package backend21.application.login

import backend21.application.ApplicationServiceException
import backend21.domain.oauthcredentials.OAuthCredentialsService
import backend21.security.SecurityUserDetails
import backend21.services.UserDetailsService
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoginAppService @Autowired constructor(
    private val userDetailsService: UserDetailsService,
    private val oauth2Service: OAuthCredentialsService)
{

    fun login(login: LoginDTO): CredentialsDTO {
        val userDetails = userDetailsService.loadUserByUsername(login.username) as SecurityUserDetails
        if (userDetails.hasSamePassword(login.password)) {
            val result = oauth2Service.getAccessToken(userDetails.username, userDetails.password)
            return CredentialsDTO(userDetails.user.toDTO(), result.toDTO())
        }
        throw ApplicationServiceException("Invalid credentials")
    }

    fun refreshToken(token: TokenDTO): OAuthCredentialsDTO
        = oauth2Service.getAccessTokenByRefreshToken(token.token).toDTO()

    companion object {
//        private val LOG = LoggerFactory.getLogger(LoginAppService::class.java)
        private val LOG = LogManager.getLogger(LoginAppService::class.java)
    }

}
