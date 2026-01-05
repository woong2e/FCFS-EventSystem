package main.kotlin.com.woong2e.couponsystem.coupon.application.response

import java.util.UUID

data class CouponResponse(
    val id: UUID,
    val title: String,
    val totalQuantity: Int,
    val issuedQuantity: Int
)
