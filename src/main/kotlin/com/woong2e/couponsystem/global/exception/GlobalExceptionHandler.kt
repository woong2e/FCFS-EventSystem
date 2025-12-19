package com.woong2e.couponsystem.global.exception

import com.woong2e.couponsystem.global.response.ApiResponse
import com.woong2e.couponsystem.global.response.status.ErrorStatus
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    // [1] 커스텀 예외
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("CustomException: {}", e.errorCode.message)
        return ApiResponse.onFailure(e.errorCode)
    }

    // [2] 유효성 검사 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val errorMessage = ex.bindingResult.allErrors.firstOrNull()?.defaultMessage ?: "요청 입력값이 올바르지 않습니다."
        log.warn("Validation Failed: {}", errorMessage)
        return ApiResponse.onFailure(ErrorStatus.INVALID_INPUT_VALUE, errorMessage)
    }

    // [3] 나머지 예외 처리
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("UnhandledException: ", e)
        return ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR)
    }
}