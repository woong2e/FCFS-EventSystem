package main.kotlin.com.woong2e.couponsystem.coupon.application.service

import main.kotlin.com.woong2e.couponsystem.coupon.api.request.CouponCreateRequest
import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponResponse
import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.Coupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import main.kotlin.com.woong2e.couponsystem.coupon.infra.redis.CouponRedisRepository
import main.kotlin.com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
    private val couponRedisRepository: CouponRedisRepository
) {

    @Transactional
    fun create(request: CouponCreateRequest): CouponResponse {
        val coupon = Coupon(
            title = request.title,
            totalQuantity = request.totalQuantity
        )
        val saved = couponRepository.save(coupon)

        return CouponResponse(
            id = saved.id,
            title = saved.title,
            totalQuantity = saved.totalQuantity,
            issuedQuantity = saved.issuedQuantity
        )
    }

    @Transactional
    fun delete(couponId: UUID) {
        val coupon = couponRepository.findById(couponId)
            .orElseThrow { CustomException(CouponErrorStatus.COUPON_NOT_FOUND) }

        // (Bulk Delete)
        issuedCouponRepository.deleteAllByCouponId(couponId)

        couponRepository.delete(coupon)
    }

    @Transactional
    fun initCouponStock(couponId: UUID, quantity: Int) {
        couponRedisRepository.initCouponStock(couponId.toString(), quantity)
    }
}