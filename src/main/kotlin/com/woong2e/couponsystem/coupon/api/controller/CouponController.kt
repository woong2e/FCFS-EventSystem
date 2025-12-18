package com.woong2e.couponsystem.coupon.api.controller

import com.woong2e.couponsystem.coupon.api.request.CouponCreateRequest
import com.woong2e.couponsystem.coupon.application.response.CouponResponse
import com.woong2e.couponsystem.coupon.application.service.CouponService
import org.springframework.http.HttpStatus
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
    fun create(@RequestBody request: CouponCreateRequest): ResponseEntity<CouponResponse> {
        val response = couponService.create(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }
}