package backend21.resources

import org.apache.catalina.core.ApplicationContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@RestController
@RequestMapping("/api")
class HealthController {
    @RequestMapping(path = ["/health"], method = [(RequestMethod.GET)])
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("green")
    }

    @PostMapping("/prueba", produces = ["text/plain;charset=UTF-8"])
    fun prueba(@RequestBody palabra: String): ResponseEntity<String> {
        val request  = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        return ResponseEntity.ok(palabra)
    }
}