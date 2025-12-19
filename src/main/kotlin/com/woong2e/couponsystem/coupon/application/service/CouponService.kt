package com.woong2e.couponsystem.coupon.application.service

import com.woong2e.couponsystem.coupon.api.request.CouponCreateRequest
import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.response.CouponResponse
import com.woong2e.couponsystem.coupon.domain.entity.Coupon
import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository
) {

    @Transactional
    fun create(request: CouponCreateRequest): CouponResponse {
        val coupon = Coupon(
            title = request.title,
            totalQuantity = request.totalQuantity
        )
        val saved = couponRepository.save(coupon)

        return CouponResponse(
            id = saved.id,
            title = saved.title,
            totalQuantity = saved.totalQuantity,
            issuedQuantity = saved.issuedQuantity
        )
    }

    @Transactional
    fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val coupon = couponRepository.findById(couponId)
            .orElseThrow { CustomException(CouponErrorStatus.COUPON_NOT_FOUND) }

        val isIssued = issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)
        if (isIssued) {
            throw CustomException(CouponErrorStatus.COUPON_ALREADY_ISSUED)
        }

        coupon.issue()

        val issuedCoupon = IssuedCoupon(
            couponId = couponId,
            userId = userId
        )
        val saved = issuedCouponRepository.save(issuedCoupon)

        return CouponIssueResponse(issuedCouponId = saved.id)
    }
}