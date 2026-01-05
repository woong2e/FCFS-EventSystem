package main.kotlin.com.woong2e.couponsystem.coupon.application.response

import java.util.UUID

data class CouponIssueResponse(
    val isSuccess: Boolean = true,
    val issuedCouponId: UUID? = null,
    val message: String? = null
) {
    companion object {
        fun asyncIssued(): CouponIssueResponse {
            return CouponIssueResponse(
                isSuccess = true,
                issuedCouponId = null,
                message = "쿠폰 발급 요청이 대기열에 추가되었습니다. 잠시 후 보관함을 확인해주세요."
            )
        }

        fun syncIssued(id: UUID): CouponIssueResponse {
            return CouponIssueResponse(
                isSuccess = true,
                issuedCouponId = id,
                message = "쿠폰 발급이 완료되었습니다."
            )
        }
    }
}
