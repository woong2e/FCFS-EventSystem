package main.kotlin.com.woong2e.couponsystem.infra.lock.impl

import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import main.kotlin.com.woong2e.couponsystem.global.response.status.ErrorStatus
import main.kotlin.com.woong2e.couponsystem.infra.lock.DistributedLockExecutor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.util.UUID

//@Primary
@Component("lettuceLockExecutor")
class LettuceLockExecutor(
    private val redisTemplate: RedisTemplate<String, String>
) : DistributedLockExecutor {

    // 1. 락 획득 Lua Script (Atomic)
    private val LOCK_SCRIPT = DefaultRedisScript(
        """
        if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then
            redis.call('pexpire', KEYS[1], ARGV[2])
            return true
        else
            return false
        end
        """.trimIndent(),
        Boolean::class.java
    )

    // 2. 락 해제 Lua Script (Atomic)
    private val UNLOCK_SCRIPT = DefaultRedisScript(
        """
        if redis.call('get', KEYS[1]) == ARGV[1] then
            return redis.call('del', KEYS[1])
        else
            return 0
        end
        """.trimIndent(),
        Long::class.java
    )

    override fun <T> execute(
        lockName: String,
        waitTime: Long,
        leaseTime: Long,
        operation: () -> T
    ): T {
        val lockValue = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        val waitTimeMillis = waitTime * 1000
        val leaseTimeMillis = leaseTime * 1000

        while ((System.currentTimeMillis() - startTime) < waitTimeMillis) {

            val isLocked = redisTemplate.execute(
                LOCK_SCRIPT,
                listOf(lockName),
                lockValue,
                leaseTimeMillis.toString()
            )

            if (isLocked == true) {
                try {
                    return operation()
                } finally {
                    redisTemplate.execute(
                        UNLOCK_SCRIPT,
                        listOf(lockName),
                        lockValue
                    )
                }
            }

            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                throw CustomException(ErrorStatus.INTERNAL_SERVER_ERROR)
            }
        }

        throw CustomException(ErrorStatus.SYSTEM_BUSY)
    }
}