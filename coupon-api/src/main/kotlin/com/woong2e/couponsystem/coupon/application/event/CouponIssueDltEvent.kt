package main.kotlin.com.woong2e.couponsystem.coupon.application.event

import main.kotlin.com.woong2e.couponsystem.coupon.value.DltSource
import java.util.UUID

data class CouponIssueDltEvent(
    val source: DltSource,
    val couponId: UUID,
    val userId: Long,
    val reason: String? = null,
)