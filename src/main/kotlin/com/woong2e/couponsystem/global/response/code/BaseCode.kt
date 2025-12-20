package com.woong2e.couponsystem.global.response.code

import org.springframework.http.HttpStatus

interface BaseCode {
    val status: HttpStatus
    val code: String
    val message: String
}