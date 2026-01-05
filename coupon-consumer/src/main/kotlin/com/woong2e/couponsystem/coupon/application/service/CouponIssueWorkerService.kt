package main.kotlin.com.woong2e.couponsystem.coupon.application.service

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

class CouponIssueWorkerService(
    private val issuedCouponRepository: IssuedCouponRepository
) {

    @Transactional
    fun issue(couponId: UUID, userId: Long) {
        val issuedCoupon = IssuedCoupon(
            couponId = couponId,
            userId = userId
        )

        issuedCouponRepository.save(issuedCoupon)
    }
}