package backend21.services

import backend21.domain.oauthcredentials.OAuthCredentialsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import backend21.domain.user.interfaces.UserRepository
import backend21.security.SecurityUserDetails
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager

@Service
class UserDetailsService @Autowired constructor(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        return user.map { SecurityUserDetails(it) }
                .orElseThrow {
                    UsernameNotFoundException("User with email $username not found")
                }
    }

    companion object {
//        private val LOG = LoggerFactory.getLogger(UserDetailsService::class.java)
        private val LOG = LogManager.getLogger(UserDetailsService::class.java)
    }
}
