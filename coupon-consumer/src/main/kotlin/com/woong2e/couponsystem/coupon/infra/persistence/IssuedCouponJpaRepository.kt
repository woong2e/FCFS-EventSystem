package main.kotlin.com.woong2e.couponsystem.coupon.infra.persistence

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IssuedCouponJpaRepository : IssuedCouponRepository, JpaRepository<IssuedCoupon, UUID> {
}