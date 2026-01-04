package main.kotlin.com.woong2e.couponsystem.coupon.infra.jpa

import jakarta.persistence.LockModeType
import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.Coupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface JpaCouponRepository : CouponRepository, JpaRepository<Coupon, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    override fun findByIdWithPessimisticLock(id: UUID): Optional<Coupon>

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Coupon c 
        SET c.issuedQuantity = c.issuedQuantity + 1 
        WHERE c.id = :id 
          AND c.issuedQuantity < c.totalQuantity
    """)
    override fun increaseIssuedQuantity(@Param("id") id: UUID): Int
}