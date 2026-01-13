package main.kotlin.com.woong2e.couponsystem.coupon.application.service

import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import java.util.UUID

interface CouponIssueService {

    fun issue(couponId: UUID, userId: Long): CouponIssueResponse?
}