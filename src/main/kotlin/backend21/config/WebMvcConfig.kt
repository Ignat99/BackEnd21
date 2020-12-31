package backend21.config

import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import backend21.wrappers.EnvironmentVarWrapper


const val MAX_AGE : Long = 3600


//@Configuration
class WebMvcConfig @Autowired constructor(private val environmentVarWrapper: EnvironmentVarWrapper) {


    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .maxAge(MAX_AGE)
                        .allowCredentials(true)
                        .allowedHeaders(
                                "x-requested-with",
                                "authorization",
                                "content-type")
                        .allowedMethods(
                                HttpPost.METHOD_NAME,
                                HttpPut.METHOD_NAME,
                                HttpGet.METHOD_NAME,
                                HttpOptions.METHOD_NAME,
                                HttpDelete.METHOD_NAME)
                        .allowedOrigins(
                                "http://${environmentVarWrapper.getEnvironmentVar("FRONTEND_HOST")}" +
                                        ":${environmentVarWrapper.getEnvironmentVar("FRONTEND_PORT")}")
            }
        }
    }
}



