package main.kotlin.com.woong2e.couponsystem.coupon.application.service.impl

import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import main.kotlin.com.woong2e.couponsystem.coupon.domain.entity.IssuedCoupon
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.CouponRepository
import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.IssuedCouponRepository
import main.kotlin.com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import main.kotlin.com.woong2e.couponsystem.global.annotaion.Bulkhead
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

@Service("atomic-query")
class AtomicQueryCouponIssueService(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
    private val transactionTemplate: TransactionTemplate
) : CouponIssueService {

    @Bulkhead(permits = 20, fair = true)
    override fun issue(couponId: UUID, userId: Long): CouponIssueResponse? {
        return transactionTemplate.execute {
            val affectedRows = couponRepository.increaseIssuedQuantity(couponId)

            if (affectedRows == 0) {
                throw CustomException(CouponErrorStatus.COUPON_OUT_OF_STOCK)
            }

            if (issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)) {
                throw CustomException(CouponErrorStatus.DUPLICATED_COUPON_ISSUE)
            }

            val saved = issuedCouponRepository.save(IssuedCoupon(couponId = couponId, userId = userId))

            CouponIssueResponse(issuedCouponId = saved.id)
        }
    }
}