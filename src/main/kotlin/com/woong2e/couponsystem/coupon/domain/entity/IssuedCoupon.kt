package com.woong2e.couponsystem.coupon.domain.entity

import com.woong2e.couponsystem.global.jpa.PrimaryKeyEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "issued_coupons")
@EntityListeners(AuditingEntityListener::class)
class IssuedCoupon(
    @Column(name = "coupon_id", nullable = false, columnDefinition = "BINARY(16)")
    val couponId: UUID,

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    val userId: Long
) : PrimaryKeyEntity() {

    @Column(nullable = false)
    var status: String = "ISSUED"

    @CreatedDate
    @Column(name = "issued_at", updatable = false)
    var issuedAt: LocalDateTime? = null
}