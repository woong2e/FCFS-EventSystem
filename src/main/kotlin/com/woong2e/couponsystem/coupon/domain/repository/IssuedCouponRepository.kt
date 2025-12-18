package com.woong2e.couponsystem.coupon.domain.repository

import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon

interface IssuedCouponRepository {

    fun save(issuedCoupon: IssuedCoupon): IssuedCoupon
}