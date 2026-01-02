package com.woong2e.couponsystem.coupon.application.facade

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.impl.NoLockCouponIssueService
import com.woong2e.couponsystem.global.exception.CustomException
import com.woong2e.couponsystem.global.response.status.ErrorStatus
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class RedissonLockCouponFacade(
    private val redissonClient: RedissonClient,
    private val noLockCouponIssueService: NoLockCouponIssueService
) {

    fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val lock = redissonClient.getLock("lock:coupon:$couponId")

        try {
            // Lock 획득
            val available = lock.tryLock(30, 3, TimeUnit.SECONDS)
            if (!available) {
                throw CustomException(ErrorStatus.SYSTEM_BUSY)
            }

            return noLockCouponIssueService.issue(couponId, userId)
        } catch (e: InterruptedException) {
            throw CustomException(ErrorStatus.INTERNAL_SERVER_ERROR)
        } finally {
            // Lock 해제
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}