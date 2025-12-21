package com.woong2e.couponsystem.global.response.status

import com.woong2e.couponsystem.global.response.code.BaseCode
import org.springframework.http.HttpStatus

enum class SuccessStatus(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : BaseCode {

    // 일반적인 성공 (200 OK)
    OK(HttpStatus.OK, "200", "요청이 성공적으로 처리되었습니다."),
    // 생성 성공 (201 Created)
    CREATED(HttpStatus.CREATED, "201", "리소스가 성공적으로 생성되었습니다."),
    // 삭제 요청 성공 (204 NO_CONTENT)
    NO_CONTENT(HttpStatus.NO_CONTENT, "204", "요청한 리소스가 성공적으로 삭제되었습니다.");
}