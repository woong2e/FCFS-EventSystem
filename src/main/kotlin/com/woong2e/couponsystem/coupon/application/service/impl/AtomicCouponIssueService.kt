package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service("atomic")
class AtomicCouponIssueService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
    private val transactionTemplate: TransactionTemplate
) : CouponIssueService {

    private data class CouponState(
        val couponCount: AtomicInteger,
        val maxLimit: Int
    )

    private val couponCounts = ConcurrentHashMap<UUID, CouponState>()

    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {

        val state = couponCounts.computeIfAbsent(couponId) { id ->
            val coupon = couponRepository.findById(id)
                .orElseThrow { CustomException(CouponErrorStatus.COUPON_NOT_FOUND) }

            CouponState(
                couponCount = AtomicInteger(coupon.issuedQuantity),
                maxLimit = coupon.totalQuantity
            )
        }

        val currentCount = state.couponCount.incrementAndGet()

        if (currentCount > state.maxLimit) {
            throw CustomException(CouponErrorStatus.COUPON_OUT_OF_STOCK)
        }

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
        } catch (e: Exception) {
            state.couponCount.decrementAndGet()
            throw e
        }
    }
}