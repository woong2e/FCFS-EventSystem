package main.kotlin.com.woong2e.couponsystem.coupon.application.event

import java.util.UUID

data class CouponIssueEvent(
    val couponId: UUID,
    val userId: Long
)
