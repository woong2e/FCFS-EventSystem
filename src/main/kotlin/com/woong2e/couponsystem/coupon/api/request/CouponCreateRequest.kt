package com.woong2e.couponsystem.coupon.api.request

data class CouponCreateRequest(
    val title: String,
    val totalQuantity: Int
)