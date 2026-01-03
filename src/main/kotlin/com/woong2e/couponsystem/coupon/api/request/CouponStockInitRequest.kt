package com.woong2e.couponsystem.coupon.api.request

import java.util.UUID

data class CouponStockInitRequest(
    val couponId: UUID,
    val quantity: Int
)
