package nl.avisi.labs.techlabelasticsearch.exceptions

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    private val kLogger = KotlinLogging.logger(this.javaClass.name)

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedExceptions(exception: Exception): ResponseEntity<String> = respondWithLogging(exception, exception.message, HttpStatus.INTERNAL_SERVER_ERROR)

    private fun respondWithLogging(exception: Exception, responseMessage: String?, status: HttpStatus): ResponseEntity<String> {
        val message = responseMessage ?: "An exception occurred while processing the request."
        kLogger.error(exception) { message }
        return ResponseEntity(message, status)
    }
}
