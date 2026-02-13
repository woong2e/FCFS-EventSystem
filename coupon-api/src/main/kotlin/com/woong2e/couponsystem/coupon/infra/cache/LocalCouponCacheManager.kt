package main.kotlin.com.woong2e.couponsystem.coupon.infra.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class LocalCouponCacheManager {

    private val cache: Cache<String, Boolean> = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 후 만료
        .maximumSize(1000)                      // 최대 1000개 쿠폰 키 보관
        .build()

    fun putSoldOut(couponId: String) {
        cache.put(couponId, true)
    }

    fun isSoldOut(couponId: String): Boolean {
        return cache.getIfPresent(couponId) == true
    }
}