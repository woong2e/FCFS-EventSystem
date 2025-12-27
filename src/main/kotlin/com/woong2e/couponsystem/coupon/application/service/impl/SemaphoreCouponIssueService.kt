package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.global.annotaion.Bulkhead
import org.springframework.stereotype.Service
import java.util.UUID

@Service("semaphore")
class SemaphoreCouponIssueService(
    private val pessimisticLockCouponIssueService: PessimisticLockCouponIssueService
) : CouponIssueService {


    @Bulkhead(permits = 15, fair = true)
    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        return pessimisticLockCouponIssueService.issue(couponId, userId)
    }
}