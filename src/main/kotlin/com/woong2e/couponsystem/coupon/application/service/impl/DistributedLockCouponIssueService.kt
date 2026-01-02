package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.coupon.domain.repository.AppliedUserRepository
import com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import com.woong2e.couponsystem.global.exception.CustomException
import com.woong2e.couponsystem.infra.lock.DistributedLockExecutor
import org.springframework.stereotype.Service
import java.util.UUID

@Service("distributed")
class DistributedLockCouponIssueService(
    private val distributedLockExecutor: DistributedLockExecutor,
    private val appliedUserRepository: AppliedUserRepository,
    private val noLockService: NoLockCouponIssueService
) : CouponIssueService {

    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val lockKey = "lock:coupon:$couponId"
        val issuedUserKey = "coupon:$couponId:issued:users"

        if (appliedUserRepository.isMember(issuedUserKey, userId)) {
            throw CustomException(CouponErrorStatus.COUPON_ALREADY_ISSUED)
        }

        return distributedLockExecutor.execute(lockKey, 30, 3) {
            val response = noLockService.issue(couponId, userId)
            appliedUserRepository.add(issuedUserKey, userId)
            response
        }
    }
}