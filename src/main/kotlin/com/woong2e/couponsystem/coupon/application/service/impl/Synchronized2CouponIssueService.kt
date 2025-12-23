package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

@Service("synchronized2")
class Synchronized2CouponIssueService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
) : CouponIssueService {

    @Transactional
    @Synchronized
    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val coupon = couponRepository.findById(couponId)
            .orElseThrow { CustomException(CouponErrorStatus.COUPON_NOT_FOUND) }

        coupon.issue()

        if (issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)) {
            throw CustomException(CouponErrorStatus.COUPON_ALREADY_ISSUED)
        }

        val issuedCoupon = IssuedCoupon(couponId = couponId, userId = userId)
        val saved = issuedCouponRepository.save(issuedCoupon)

        return CouponIssueResponse(issuedCouponId = saved.id)
    }
}