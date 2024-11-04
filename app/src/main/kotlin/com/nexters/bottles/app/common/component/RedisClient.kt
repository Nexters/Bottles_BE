package com.nexters.bottles.app.common.component

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisClient(
    private var redisTemplate: RedisTemplate<String, Any>
) {

    fun setValue(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun getValue(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    fun incrementValue(key: String, delta: Long): Long? {
        return redisTemplate.opsForValue().increment(key, delta)
    }
}
