package main.kotlin.com.woong2e.couponsystem.coupon.application.port.out

import java.util.UUID

interface CouponIssueEventPublisher {
    fun publish(couponId: UUID, userId: Long)
}