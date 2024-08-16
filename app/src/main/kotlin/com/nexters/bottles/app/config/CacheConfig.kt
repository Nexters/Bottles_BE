package com.nexters.bottles.app.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@EnableCaching
@Configuration
class CacheConfig {

    @Bean
    fun caches(): List<CaffeineCache> {
        return CacheType.entries
            .map { cache ->
                CaffeineCache(
                    cache.cacheName,
                    Caffeine.newBuilder().recordStats()
                        .expireAfterAccess(cache.expireAfterAccess, cache.timeUnit)
                        .maximumSize(cache.maximumSize)
                        .build()
                )
            }.toList()
    }

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(caches())
        return cacheManager
    }
}
