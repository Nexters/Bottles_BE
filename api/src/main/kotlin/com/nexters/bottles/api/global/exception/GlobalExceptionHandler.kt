package com.nexters.bottles.api.global.exception

import com.nexters.bottles.app.common.exception.ConflictException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger { }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: IllegalArgumentException): ErrorResponseDto {
        log.warn { "Error occured at path: ${request.requestURI}, message: ${e.message} stackTrace: ${e.stackTraceToString()}" }
        return ErrorResponseDto(e.message)
    }

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handle(request: HttpServletRequest, e: IllegalStateException): ErrorResponseDto {
        log.warn { "Error occured at path: ${request.requestURI}, message: ${e.message} stackTrace: ${e.stackTraceToString()}" }
        return ErrorResponseDto(e.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun handle(request: HttpServletRequest, e: UnauthorizedException): ErrorResponseDto {
        log.warn { "Error occured at path: ${request.requestURI}, message: ${e.message} stackTrace: ${e.stackTraceToString()}" }
        return ErrorResponseDto(e.message)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    fun handle(request: HttpServletRequest, e: ConflictException): ErrorResponseDto {
        log.warn { "Error occured at path: ${request.requestURI}, message: ${e.message} stackTrace: ${e.stackTraceToString()}" }
        return ErrorResponseDto(e.message)
    }
}
