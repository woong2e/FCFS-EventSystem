package main.kotlin.com.woong2e.couponsystem.coupon.application.service.impl

import main.kotlin.com.woong2e.couponsystem.coupon.application.port.out.CouponIssueEventPublisher
import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import main.kotlin.com.woong2e.couponsystem.coupon.infra.redis.CouponRedisRepository
import main.kotlin.com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import main.kotlin.com.woong2e.couponsystem.global.response.status.ErrorStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service("asyncLua")
class AsyncLuaCouponIssueService(
    private val couponRedisRepository: CouponRedisRepository,
    private val couponIssueEventPublisher: CouponIssueEventPublisher
) : CouponIssueService {

    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val result = couponRedisRepository.issueRequest(couponId.toString(), userId)

        when (result) {
            "DUPLICATED" -> throw CustomException(CouponErrorStatus.COUPON_ALREADY_ISSUED)
            "SOLD_OUT" -> throw CustomException(CouponErrorStatus.COUPON_OUT_OF_STOCK)
            "SUCCESS" -> {
                couponIssueEventPublisher.publish(couponId, userId)

                return CouponIssueResponse.asyncIssued()
            }
            else -> throw CustomException(ErrorStatus.SYSTEM_BUSY)
        }
    }
}