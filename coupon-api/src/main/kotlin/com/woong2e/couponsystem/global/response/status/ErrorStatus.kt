package main.kotlin.com.woong2e.couponsystem.global.response.status

import main.kotlin.com.woong2e.couponsystem.global.response.code.BaseErrorStatus
import org.springframework.http.HttpStatus

enum class ErrorStatus(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
    ) : BaseErrorStatus {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 오류입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G002", "올바르지 않은 입력값입니다."),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    // 세마포어/트래픽 초과 시 (Fail-Fast 또는 타임아웃 발생 시)
    SYSTEM_BUSY(HttpStatus.SERVICE_UNAVAILABLE, "S002", "현재 접속량이 많아 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요."),
    // 대기 시간 초과 (Blocking 방식에서 타임아웃 발생 시)
    COUPON_ISSUE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "S003", "대기 시간이 초과되었습니다. 다시 시도해주세요.");
}