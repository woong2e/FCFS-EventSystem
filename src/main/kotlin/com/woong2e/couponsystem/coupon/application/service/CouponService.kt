package com.woong2e.couponsystem.coupon.application.service

import com.woong2e.couponsystem.coupon.api.request.CouponCreateRequest
import com.woong2e.couponsystem.coupon.application.response.CouponResponse
import com.woong2e.couponsystem.coupon.domain.entity.Coupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponService(
    private val couponRepository: CouponRepository,
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
}