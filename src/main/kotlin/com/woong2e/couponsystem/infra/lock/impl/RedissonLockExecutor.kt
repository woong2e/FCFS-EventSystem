package com.woong2e.couponsystem.infra.lock.impl

import com.woong2e.couponsystem.global.exception.CustomException
import com.woong2e.couponsystem.global.response.status.ErrorStatus
import com.woong2e.couponsystem.infra.lock.DistributedLockExecutor
import org.redisson.api.RedissonClient
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Primary
@Component("redissonLockExecutor")
class RedissonLockExecutor(
    private val redissonClient: RedissonClient
) : DistributedLockExecutor {

    override fun <T> execute(
        lockName: String,
        waitTime: Long,
        leaseTime: Long,
        operation: () -> T
    ): T {
        val lock = redissonClient.getLock(lockName)

        try {
            val available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)
            if (!available) {
                throw CustomException(ErrorStatus.SYSTEM_BUSY)
            }

            return operation()
        } catch (e: InterruptedException) {
            throw CustomException(ErrorStatus.INTERNAL_SERVER_ERROR)
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}