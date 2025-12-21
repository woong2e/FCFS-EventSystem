package com.woong2e.couponsystem.coupon.domain.repository

import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import java.util.UUID

interface IssuedCouponRepository {

    fun save(issuedCoupon: IssuedCoupon): IssuedCoupon

    fun existsByCouponIdAndUserId(couponId: UUID, userId: Long): Boolean

    fun deleteAllByCouponId(couponId: UUID)
}