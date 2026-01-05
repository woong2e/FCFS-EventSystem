package main.kotlin.com.woong2e.couponsystem.coupon.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import main.kotlin.com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import main.kotlin.com.woong2e.couponsystem.global.jpa.PrimaryKeyEntity

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
            throw CustomException(CouponErrorStatus.COUPON_OUT_OF_STOCK)
        }
        this.issuedQuantity++
    }
}