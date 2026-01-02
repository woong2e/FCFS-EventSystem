package com.woong2e.couponsystem.coupon.domain.repository

interface AppliedUserRepository {

    fun add(key: String, userId: Long): Long

    fun isMember(key: String, userId: Long): Boolean
}