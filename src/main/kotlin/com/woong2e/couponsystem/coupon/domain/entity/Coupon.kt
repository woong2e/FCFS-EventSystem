package com.woong2e.couponsystem.coupon.domain.entity

import com.woong2e.couponsystem.global.jpa.PrimaryKeyEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "coupons")
class Coupon(
    val title: String,
    val totalQuantity: Int,
    var issuedQuantity: Int = 0
) : PrimaryKeyEntity() {

    fun available(): Boolean {
        return issuedQuantity < totalQuantity
    }

    fun issue() {
        if (!available()) {
            throw IllegalArgumentException("재고가 모두 소진되었습니다.")
        }
        this.issuedQuantity++
    }
}