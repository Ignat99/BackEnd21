package backend21.controller

import backend21.domain.DomainException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import backend21.wrappers.LoggerWrapper
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class GlobalControllerExceptionHandler @Autowired constructor(private val logger: LoggerWrapper) {

    @ExceptionHandler(EntityNotFoundException::class, DomainException::class)
    private fun handleException(e: RuntimeException, request: HttpServletRequest): ResponseEntity<Any>{
        logger.error("${request.requestURI} throws ${e.message}")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }
}
