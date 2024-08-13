package com.nexters.bottles.app.config

import java.util.concurrent.TimeUnit

enum class CacheType(
    val cacheName: String,
    val expireAfterAccess: Long,
    val timeUnit: TimeUnit,
    val maximumSize: Long
) {
    LETTER_QUESTION("questions", 60, TimeUnit.MINUTES, 1),
    PING_PONG_BOTTLE_LIST("pingPongBottleList", 60, TimeUnit.MINUTES, 100),
    PING_PONG_BOTTLE("pingPongBottle", 60, TimeUnit.MINUTES, 100),
    ;
}
