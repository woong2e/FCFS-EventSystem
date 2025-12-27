package com.woong2e.couponsystem.coupon.application.service.impl

import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import com.woong2e.couponsystem.global.annotaion.Bulkhead
import com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service("atomic-query")
class AtomicQueryCouponIssueService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository
) : CouponIssueService {

    @Bulkhead(permits = 20, fair = true)
    @Transactional
    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse {
        val affectedRows = couponRepository.increaseIssuedQuantity(couponId)

        if (affectedRows == 0) {
            throw CustomException(CouponErrorStatus.COUPON_OUT_OF_STOCK)
        }

        if (issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)) {
            throw CustomException(CouponErrorStatus.DUPLICATED_COUPON_ISSUE)
        }

        val saved = issuedCouponRepository.save(IssuedCoupon(couponId = couponId, userId = userId))

        return CouponIssueResponse(issuedCouponId = saved.id)
    }
}