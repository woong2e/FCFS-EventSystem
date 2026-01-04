package main.kotlin.com.woong2e.couponsystem.coupon.domain.repository

import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.Coupon
import java.util.Optional
import java.util.UUID

interface CouponRepository {

    fun save(coupon: Coupon): Coupon

    fun findById(id: UUID): Optional<Coupon>

    fun findByIdWithPessimisticLock(id: UUID): Optional<Coupon>

    fun increaseIssuedQuantity(id: UUID): Int

    fun delete(coupon: Coupon)
}