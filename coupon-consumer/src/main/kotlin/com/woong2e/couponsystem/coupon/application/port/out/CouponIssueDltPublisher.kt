package main.kotlin.com.woong2e.couponsystem.coupon.application.port.out

import main.kotlin.com.woong2e.couponsystem.coupon.consumer.event.CouponIssueDltEvent

interface CouponIssueDltPublisher {
    fun publish(event: CouponIssueDltEvent)
}
