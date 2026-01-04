package main.kotlin.com.woong2e.couponsystem.coupon.api.controller

import main.kotlin.com.woong2e.couponsystem.coupon.api.request.CouponCreateRequest
import main.kotlin.com.woong2e.couponsystem.coupon.api.request.CouponIssueRequest
import main.kotlin.com.woong2e.couponsystem.coupon.api.request.CouponStockInitRequest
import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import main.kotlin.com.woong2e.couponsystem.coupon.application.response.CouponResponse
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponIssueService
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponService
import main.kotlin.com.woong2e.couponsystem.coupon.status.CouponErrorStatus
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import main.kotlin.com.woong2e.couponsystem.global.response.ApiResponse
import main.kotlin.com.woong2e.couponsystem.global.response.status.SuccessStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/coupons")
class CouponController(
    private val couponService: CouponService,
    private val couponIssueServiceMap: Map<String, CouponIssueService>
) {

    @PostMapping
    fun create(
        @RequestBody request: CouponCreateRequest,
    ): ResponseEntity<ApiResponse<CouponResponse>> {
        val response = couponService.create(request)
        return ApiResponse.onSuccess(SuccessStatus.CREATED, response)
    }

    @PostMapping("/issue")
    fun issue(
        @RequestBody request: CouponIssueRequest,
        @RequestParam(defaultValue = "noLock") type: String
    ): ResponseEntity<ApiResponse<CouponIssueResponse>> {
        val couponIssueService = couponIssueServiceMap[type]
            ?: throw CustomException(CouponErrorStatus.INVALID_COUPON_ISSUE_SERVICE_TYPE)

        val response = couponIssueService.issue(request.couponId, request.userId)
        return ApiResponse.onSuccess(SuccessStatus.OK, response)
    }

    @PostMapping("/init-stock")
    fun initCouponStock(
        @RequestBody request: CouponStockInitRequest
    ) {
        couponService.initCouponStock(request.couponId, request.quantity)
    }

    @DeleteMapping("/{couponId}")
    fun delete(@PathVariable couponId: UUID): ResponseEntity<ApiResponse<String>> {
        couponService.delete(couponId)
        return ApiResponse.onSuccess(SuccessStatus.NO_CONTENT)
    }
}