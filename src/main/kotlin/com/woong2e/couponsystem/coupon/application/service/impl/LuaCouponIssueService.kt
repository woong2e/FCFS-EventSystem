package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import com.woong2e.couponsystem.coupon.infra.CouponRedisRepository
import com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import com.woong2e.couponsystem.global.exception.CustomException
import com.woong2e.couponsystem.global.response.status.ErrorStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service("lua")
class LuaCouponIssueService(
    private val couponRedisRepository: CouponRedisRepository,
    private val issuedCouponRepository: IssuedCouponRepository
) : CouponIssueService {

    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val result = couponRedisRepository.issueRequest(couponId.toString(), userId)

        when (result) {
            "DUPLICATED" -> throw CustomException(CouponErrorStatus.COUPON_ALREADY_ISSUED)
            "SOLD_OUT" -> throw CustomException(CouponErrorStatus.COUPON_OUT_OF_STOCK)
            "SUCCESS" -> {
                val issuedCoupon = IssuedCoupon(couponId = couponId, userId = userId)
                val saved = issuedCouponRepository.save(issuedCoupon)
                return CouponIssueResponse(issuedCouponId = saved.id)
            }
            else -> throw CustomException(ErrorStatus.SYSTEM_BUSY)
        }
    }
}