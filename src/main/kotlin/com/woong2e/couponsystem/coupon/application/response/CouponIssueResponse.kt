package com.woong2e.couponsystem.coupon.application.response

import java.util.UUID

data class CouponIssueResponse(
    val result: String = "SUCCESS",
    val issuedCouponId: UUID? = null
)
