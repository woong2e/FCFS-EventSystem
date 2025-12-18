package com.woong2e.couponsystem.coupon.infra

import com.woong2e.couponsystem.coupon.domain.entity.Coupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaCouponRepository : CouponRepository, JpaRepository<Coupon, UUID> {
}