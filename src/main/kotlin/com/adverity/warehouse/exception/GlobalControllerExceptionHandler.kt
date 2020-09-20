package com.adverity.warehouse.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
internal class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = [EntityNotFoundException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEntityNotFoundException(req: HttpServletRequest?, ex: EntityNotFoundException): ErrorResponse {
        return ErrorResponse(ex.message?.let { listOf(it) } ?: emptyList())
    }

    @ExceptionHandler(value = [MetricCannotBeGroupedException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMetricCannotBeGroupedException(req: HttpServletRequest?, ex: MetricCannotBeGroupedException): ErrorResponse {
        return ErrorResponse(ex.message?.let { listOf(it) } ?: emptyList())
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(req: HttpServletRequest?, ex: MethodArgumentNotValidException): ErrorResponse {
        val result = ex.bindingResult
        val fieldErrors = result.fieldErrors
        return ErrorResponse(fieldErrors.mapNotNull { it.defaultMessage })
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnknownException(ex: Exception): ErrorResponse? {
        logger.error("Internal application error", ex)
        return ErrorResponse(ex.message?.let { listOf(it) } ?: emptyList())
    }

}