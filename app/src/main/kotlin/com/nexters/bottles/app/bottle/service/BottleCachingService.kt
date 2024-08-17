package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.config.CacheType.Name.PING_PONG_BOTTLE
import com.nexters.bottles.app.config.CacheType.Name.PING_PONG_BOTTLE_LIST
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class BottleCachingService(
    private val bottleService: BottleService
) {

    @Cacheable(PING_PONG_BOTTLE, key = "#bottleId")
    fun getPingPongBottle(bottleId: Long): Bottle {
        return bottleService.getPingPongBottle(bottleId)
    }

    @Cacheable(PING_PONG_BOTTLE_LIST, key = "#userId")
    fun getPingPongBottles(userId: Long): List<Bottle> {
        return bottleService.getPingPongBottles(userId)
    }

    @Caching(
        evict = [
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "#sourceUserId"),
            CacheEvict(PING_PONG_BOTTLE_LIST, key = "#targetUserId")
        ]
    )
    fun evictPingPongList(sourceUserId: Long, targetUserId: Long) {
    }
}
