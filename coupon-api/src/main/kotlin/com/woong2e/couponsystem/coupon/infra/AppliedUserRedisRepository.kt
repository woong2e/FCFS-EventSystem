package main.kotlin.com.woong2e.couponsystem.coupon.infra

import main.kotlin.com.woong2e.couponsystem.coupon.domain.repository.AppliedUserRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class AppliedUserRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) : AppliedUserRepository {

    override fun add(key: String, userId: Long): Long {
        val result = redisTemplate.opsForSet().add(key, userId.toString()) ?: 0L

        if (result > 0) {
            redisTemplate.expire(key, Duration.ofHours(1))
        }
        return redisTemplate.opsForSet().add(key, userId.toString()) ?: 0L
    }

    override fun isMember(key: String, userId: Long): Boolean {
        return redisTemplate.opsForSet().isMember(key, userId.toString()) ?: false
    }
}