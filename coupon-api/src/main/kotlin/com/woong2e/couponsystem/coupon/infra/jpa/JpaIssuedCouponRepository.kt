package main.kotlin.com.woong2e.couponsystem.coupon.infra.jpa

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface JpaIssuedCouponRepository : IssuedCouponRepository, JpaRepository<IssuedCoupon, UUID> {

    @Modifying
    @Query("DELETE FROM IssuedCoupon ic WHERE ic.couponId = :couponId")
    override fun deleteAllByCouponId(couponId: UUID)
}