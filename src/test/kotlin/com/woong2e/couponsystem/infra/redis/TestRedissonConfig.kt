package com.woong2e.couponsystem.infra.redis

import org.mockito.Mockito
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@Profile("test")
@TestConfiguration
class TestRedissonConfig {

    @Bean
    fun redissonClient(): RedissonClient = Mockito.mock(RedissonClient::class.java)
}