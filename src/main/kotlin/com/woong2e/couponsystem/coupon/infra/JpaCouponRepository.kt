package com.woong2e.couponsystem.coupon.infra

import com.woong2e.couponsystem.coupon.domain.entity.Coupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface JpaCouponRepository : CouponRepository, JpaRepository<Coupon, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    override fun findByIdWithPessimisticLock(id: UUID): Optional<Coupon>
}