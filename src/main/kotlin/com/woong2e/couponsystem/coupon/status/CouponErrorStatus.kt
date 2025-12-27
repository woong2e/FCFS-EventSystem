package com.woong2e.couponsystem.coupon.status

import com.woong2e.couponsystem.global.response.code.BaseErrorStatus
import org.springframework.http.HttpStatus

enum class CouponErrorStatus(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorStatus {

    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 쿠폰입니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "C002", "이미 쿠폰이 발급되었습니다."),
    COUPON_OUT_OF_STOCK(HttpStatus.CONFLICT, "C003", "쿠폰이 모두 소진되었습니다."),
    INVALID_COUPON_ISSUE_SERVICE_TYPE(HttpStatus.BAD_REQUEST, "C004", "지원하지 않는 쿠폰 발급 방식입니다."),
    DUPLICATED_COUPON_ISSUE(HttpStatus.CONFLICT, "C004", "이미 발급된 쿠폰입니다.");
}