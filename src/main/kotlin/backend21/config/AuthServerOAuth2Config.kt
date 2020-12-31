package backend21.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import backend21.services.UserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.security.oauth2.provider.token.TokenStore
import javax.sql.DataSource
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

    const val ACCESS_TOKEN =  500000000
    const val REFRESH_TOKEN = 1000000000
    const val CLIENT_ID = "client_id"
    const val SECRET = "secret"
    const val RESOURCE_ID = "resource_id"


@Configuration
@EnableAuthorizationServer
class AuthServerOAuth2Config(@Autowired
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService,
        private val dataSource: DataSource) : AuthorizationServerConfigurerAdapter() 
{




    @Bean
    fun tokenStore(): TokenStore {
        return InMemoryTokenStore()
    }
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
                .authenticationManager(authenticationManager).tokenStore(tokenStore())
                    .userDetailsService(userDetailsService)

    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val tokenServices = DefaultTokenServices()
        tokenServices.setSupportRefreshToken(true)
        tokenServices.setTokenStore(tokenStore())
        //tokenServices.setClientDetailsService(JdbcClientDetailsService(dataSource))
        return tokenServices
    }


    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients()
    }


    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients
                .inMemory()
                .withClient(CLIENT_ID)
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
                .secret(SECRET).accessTokenValiditySeconds(ACCESS_TOKEN)
                    .refreshTokenValiditySeconds(REFRESH_TOKEN)
    }

}
