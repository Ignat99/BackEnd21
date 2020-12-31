package backend21.resources

import backend21.domain.log.ApiLog
import backend21.domain.log.interfaces.ApiLogRepository
import backend21.domain.oauthcredentials.CredentialsException
import backend21.security.SecurityUserDetails
import org.apache.catalina.core.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Component
open class BaseController {

    @Autowired
    private lateinit var apiLogRepository: ApiLogRepository


    fun return201(): ResponseEntity<String> {
        val location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").build().toUri()

        return ResponseEntity.created(location).build<String>()
    }

    fun return204(): ResponseEntity<String> = ResponseEntity.noContent().build()


    fun return200(): ResponseEntity<String> = ResponseEntity.ok().build()


    fun checkCredentials(data:String, callback: (id:String) -> ResponseEntity<Any>): ResponseEntity<Any> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal
        val request  = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        if (userDetails is SecurityUserDetails) {
            apiLogRepository.save(ApiLog(null, userDetails.user.id!!, request.method, request.requestURI, data, Date()))
            return callback(userDetails.user.id!!)
        }
        throw CredentialsException("You must logged to do this action")
    }
}