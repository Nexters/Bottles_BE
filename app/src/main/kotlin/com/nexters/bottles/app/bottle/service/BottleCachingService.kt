package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class BottleCachingService(
    private val bottleService: BottleService
) {

    @Cacheable("pingPongBottle", key = "#bottleId")
    fun getPingPongBottle(bottleId: Long): Bottle {
        return bottleService.getPingPongBottle(bottleId)
    }

    @Cacheable("pingPongBottleList", key = "#userId")
    fun getPingPongBottles(userId: Long): List<Bottle> {
        return bottleService.getPingPongBottles(userId)
    }
}
