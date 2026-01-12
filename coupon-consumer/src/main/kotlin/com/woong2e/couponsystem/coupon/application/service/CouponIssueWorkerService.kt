package main.kotlin.com.woong2e.couponsystem.coupon.application.service

import main.kotlin.com.woong2e.couponsystem.coupon.consumer.event.CouponIssueEvent
import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponBatchRepository
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CouponIssueWorkerService(
    private val issuedCouponRepository: IssuedCouponRepository,
    private val issuedCouponBatchRepository: IssuedCouponBatchRepository
) {

    @Transactional
    fun issue(couponId: UUID, userId: Long) {
        val issuedCoupon = IssuedCoupon(
            couponId = couponId,
            userId = userId
        )

        issuedCouponRepository.save(issuedCoupon)
    }

    @Transactional
    fun issueRequestBatch(events: List<CouponIssueEvent>) {
        val coupons = events.map { IssuedCoupon(it.couponId, it.userId) }
        issuedCouponBatchRepository.batchInsert(coupons)
    }
}