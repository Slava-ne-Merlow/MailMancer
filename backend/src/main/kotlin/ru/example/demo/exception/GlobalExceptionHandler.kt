package ru.example.demo.exception


import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.example.demo.dto.response.ErrorResponse
import ru.example.demo.exception.type.*
import ru.example.demo.util.Loggable

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler : Loggable() {

    @ExceptionHandler(BadRequestException::class)
    fun handleIllegalArgumentException(exception: BadRequestException): ResponseEntity<ErrorResponse> {
        logger.warn("BadRequestException: {}", exception.message)
        return ResponseEntity.status(400).body(ErrorResponse(exception.message ?: "BAD REQUEST"))
    }

    @ExceptionHandler(EntityAlreadyExistsException::class)
    fun handleEntityAlreadyExistsException(exception: EntityAlreadyExistsException): ResponseEntity<ErrorResponse> {
        logger.warn("EntityAlreadyExistsException: {}", exception.message)
        return ResponseEntity.status(409).body(ErrorResponse(exception.message ?: "CONFLICT"))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("NotFoundException: {}", exception.message)
        return ResponseEntity.status(404).body(ErrorResponse(exception.message ?: "NOT FOUND"))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(exception: UnauthorizedException): ResponseEntity<ErrorResponse> {
        logger.warn("UnauthorizedException: {}", exception.message)
        return ResponseEntity.status(401).body(ErrorResponse(exception.message ?: "UNAUTHORIZED"))
    }

    @ExceptionHandler(ExpiredTokenException::class)
    fun handleExpiredTokenException(exception: ExpiredTokenException): ResponseEntity<ErrorResponse> {
        logger.warn("ExpiredTokenException: {}", exception.message)
        return ResponseEntity.status(410).body(ErrorResponse(exception.message ?: "TOKEN EXPIRED"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error", exception)
        return ResponseEntity.status(500).body(ErrorResponse("Internal server error"))
    }
}
