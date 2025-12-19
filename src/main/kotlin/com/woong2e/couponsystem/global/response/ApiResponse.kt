package com.woong2e.couponsystem.global.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.woong2e.couponsystem.global.response.code.BaseCode
import com.woong2e.couponsystem.global.response.code.BaseErrorStatus
import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: T? = null
) {
    companion object {
        // [성공] 데이터가 있는 경우 & 없는 경우 (result 기본값 null 활용)
        fun <T> onSuccess(code:
                          BaseCode, result: T? = null): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity
                .status(code.status)
                .body(
                    ApiResponse(
                        isSuccess = true,
                        code = code.code,
                        message = code.message,
                        result = result
                    )
                )
        }

        // [실패] 커스텀 메시지 없이 기본 메시지 사용
        fun onFailure(code: BaseErrorStatus): ResponseEntity<ApiResponse<Unit>> {
            return onFailure(code, code.message)
        }

        // [실패] 커스텀 메시지 사용
        fun onFailure(code: BaseErrorStatus, message: String): ResponseEntity<ApiResponse<Unit>> {
            return ResponseEntity
                .status(code.status)
                .body(
                    ApiResponse(
                        isSuccess = false,
                        code = code.code,
                        message = message,
                        result = null
                    )
                )
        }
    }
}