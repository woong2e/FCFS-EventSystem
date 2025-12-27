package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.global.annotaion.Bulkhead
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.Semaphore

@Service("semaphore")
class SemaphoreCouponIssueService(
    private val pessimisticService: CouponIssueService
) : CouponIssueService {

    private val semaphore = Semaphore(15, true)

    @Bulkhead(permits = 15, fair = true, waitTime = 10)
    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        return pessimisticService.issue(couponId, userId)
    }
}