package backend21.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer


@Configuration
@EnableResourceServer

class WebSecurityConfig : ResourceServerConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/swagger-resources/**","/v2/api-docs","/swagger-ui.html",
                    "/login**", "/api/prueba/**","/api/keywords","/api/email","/api/login",
                    "/api/refreshtoken", "/api/user", "/keywords", "/api/health")
                .permitAll()
                .anyRequest()
                .authenticated()
    }

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        // @formatter:off
        resources.resourceId("resource_id")
        // @formatter:on
    }

}
