package com.woong2e.couponsystem.infra.lock

interface DistributedLockExecutor {

    fun <T> execute(
        lockName: String,
        waitTime: Long,
        leaseTime: Long,
        operation: () -> T
    ): T
}