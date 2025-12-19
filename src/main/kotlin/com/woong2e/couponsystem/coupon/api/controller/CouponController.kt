package com.woong2e.couponsystem.coupon.api.controller

import com.woong2e.couponsystem.coupon.api.request.CouponCreateRequest
import com.woong2e.couponsystem.coupon.api.request.CouponIssueRequest
import com.woong2e.couponsystem.coupon.application.response.CouponIssueResponse
import com.woong2e.couponsystem.coupon.application.response.CouponResponse
import com.woong2e.couponsystem.coupon.application.service.CouponService
import com.woong2e.couponsystem.global.response.ApiResponse
import com.woong2e.couponsystem.global.response.status.SuccessStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupons")
class CouponController(
    private val couponService: CouponService
) {

    @PostMapping
    fun create(@RequestBody request: CouponCreateRequest): ResponseEntity<ApiResponse<CouponResponse>> {
        val response = couponService.create(request)
        return ApiResponse.onSuccess(SuccessStatus.CREATED, response)
    }

    @PostMapping("/issue")
    fun issue(@RequestBody request: CouponIssueRequest): ResponseEntity<ApiResponse<CouponIssueResponse>> {
        val response = couponService.issue(request.couponId, request.userId)
        return ApiResponse.onSuccess(SuccessStatus.OK, response)
    }
}