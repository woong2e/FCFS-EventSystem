package main.kotlin.com.woong2e.couponsystem.coupon.infra.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class CouponRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {

    // KEYS[1]: coupon:{id}:issued:users (발급 유저 목록 Set)
    // KEYS[2]: coupon:{id}:total_quantity (남은 수량 Count)
    // ARGV[1]: userId
    private val ISSUE_SCRIPT = DefaultRedisScript(
        """
        -- 1. 중복 발급 체크 (Set에 이미 있으면 2 반환)
        if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then
            return 'DUPLICATED'
        end

        -- 2. 수량 체크 (수량이 0보다 작거나 같으면 3 반환)
        local stock = tonumber(redis.call('GET', KEYS[2]) or '0')
        if stock <= 0 then
            return 'SOLD_OUT'
        end

        -- 3. 발급 처리 (수량 감소 + 유저 추가)
        redis.call('DECR', KEYS[2])
        redis.call('SADD', KEYS[1], ARGV[1])
        return 'SUCCESS'
        """.trimIndent(),
        String::class.java
    )

    fun issueRequest(couponId: String, userId: Long): String {
        val issuedUserKey = "coupon:$couponId:issued:users"
        val stockKey = "coupon:$couponId:total_quantity"

        return redisTemplate.execute(
            ISSUE_SCRIPT,
            listOf(issuedUserKey, stockKey), // KEYS
            userId.toString()                // ARGV
        )
    }

    fun initCouponStock(couponId: String, quantity: Int) {
        val stockKey = "coupon:$couponId:total_quantity"

        redisTemplate.opsForValue().set(
            stockKey,
            quantity.toString(),
            Duration.ofHours(1)
        )    }
}