package main.kotlin.com.woong2e.couponsystem.global.aop

import main.kotlin.com.woong2e.couponsystem.global.annotaion.Bulkhead
import main.kotlin.com.woong2e.couponsystem.global.exception.CustomException
import main.kotlin.com.woong2e.couponsystem.global.response.status.ErrorStatus
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@Aspect
@Component
class BulkheadAspect {

    private val semaphores = ConcurrentHashMap<String, Semaphore>()

    @Around("@annotation(bulkhead)")
    fun applyBulkhead(joinPoint: ProceedingJoinPoint, bulkhead: Bulkhead): Any? {
        val key = resolveKey(joinPoint, bulkhead)

        val semaphore = semaphores.computeIfAbsent(key) {
            Semaphore(bulkhead.permits, bulkhead.fair)
        }

        // 대기열이 너무 길면 아예 거절 (일단 안함)
        // if (semaphore.queueLength > 100) throw CustomException(CouponErrorStatus.SYSTEM_BUSY)

        try {
            val acquired = semaphore.tryAcquire(bulkhead.waitTime, TimeUnit.SECONDS)
            if (!acquired) {
                throw CustomException(ErrorStatus.COUPON_ISSUE_TIMEOUT)
            }

            return try {
                joinPoint.proceed()
            } finally {
                semaphore.release()
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw CustomException(ErrorStatus.SYSTEM_ERROR)
        }
    }

    private fun resolveKey(joinPoint: ProceedingJoinPoint, bulkhead: Bulkhead): String {
        if (bulkhead.key.isNotEmpty()) {
            return bulkhead.key
        }

        val signature = joinPoint.signature as MethodSignature
        val className = signature.declaringTypeName
        val methodName = signature.name

        return "$className.$methodName"
    }
}