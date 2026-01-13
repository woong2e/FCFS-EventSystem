package main.kotlin.com.woong2e.couponsystem.coupon.application.service.impl

import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import main.kotlin.com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock

@Service("reentrant")
class ReentrantLockCouponIssueService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
    private val transactionTemplate: TransactionTemplate
) : CouponIssueService {

    private val lock = ReentrantLock(true)

    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse? {
        lock.lock()

        try {
            return transactionTemplate.execute {
                val coupon = couponRepository.findById(couponId)
                    .orElseThrow { CustomException(CouponErrorStatus.COUPON_NOT_FOUND) }

                coupon.issue()

                if (issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)) {
                    throw CustomException(CouponErrorStatus.COUPON_ALREADY_ISSUED)
                }

                val issuedCoupon = IssuedCoupon(couponId = couponId, userId = userId)
                val saved = issuedCouponRepository.save(issuedCoupon)

                CouponIssueResponse(issuedCouponId = saved.id)
            }
        } finally {
            lock.unlock()
        }
    }
}