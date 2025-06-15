package com.project.mukchoice.config

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.converter.HttpMessageNotReadableException
import org.slf4j.LoggerFactory

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<String> {
        logger.error("HttpMessageNotReadableException 발생", ex)
        val cause = ex.cause
        if (cause is MismatchedInputException) {
            val fieldNames = cause.path.joinToString(", ") { it.fieldName ?: "[알 수 없음]" }
            val message = "입력값 오류: 필드명 = $fieldNames"
            return ResponseEntity(message, HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity("요청 본문을 읽을 수 없습니다.", HttpStatus.BAD_REQUEST)
    }
}
