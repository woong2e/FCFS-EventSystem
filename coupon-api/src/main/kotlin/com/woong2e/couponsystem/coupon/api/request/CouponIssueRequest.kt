package main.kotlin.com.woong2e.couponsystem.coupon.api.request

import java.util.UUID

data class CouponIssueRequest(
    val userId: Long,
    val couponId: UUID
)
