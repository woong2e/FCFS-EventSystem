package main.kotlin.com.woong2e.couponsystem.coupon.domain.repository

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon

interface IssuedCouponBatchRepository {

    fun batchInsert(coupons: List<IssuedCoupon>)
}