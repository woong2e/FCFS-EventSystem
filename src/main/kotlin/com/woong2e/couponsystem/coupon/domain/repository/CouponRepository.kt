package com.woong2e.couponsystem.coupon.domain.repository

import com.woong2e.couponsystem.coupon.domain.entity.Coupon
import java.util.Optional
import java.util.UUID

interface CouponRepository {

    fun save(coupon: Coupon): Coupon

    fun findById(id: UUID): Optional<Coupon>

    fun delete(coupon: Coupon)
}