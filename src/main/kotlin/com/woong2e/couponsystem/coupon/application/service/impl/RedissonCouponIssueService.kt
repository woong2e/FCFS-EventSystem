package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.facade.RedissonLockCouponFacade
import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import org.springframework.stereotype.Service
import java.util.UUID

@Service("redisson")
class RedissonCouponIssueService(
    private val redissonLockCouponFacade: RedissonLockCouponFacade
) : CouponIssueService {

    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        return redissonLockCouponFacade.issue(couponId, userId)
    }
}