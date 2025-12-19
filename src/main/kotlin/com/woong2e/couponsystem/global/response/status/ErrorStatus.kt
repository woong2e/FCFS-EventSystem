package com.woong2e.couponsystem.global.response.status

import com.woong2e.couponsystem.global.response.code.BaseErrorStatus
import org.springframework.http.HttpStatus

enum class ErrorStatus(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
    ) : BaseErrorStatus {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 오류입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G002", "올바르지 않은 입력값입니다.");
}