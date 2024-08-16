package com.nexters.bottles.app.config

import java.util.concurrent.TimeUnit

enum class CacheType(
    val cacheName: String,
    val expireAfterAccess: Long,
    val timeUnit: TimeUnit,
    val maximumSize: Long
) {
    LETTER_QUESTION(Name.LETTER_QUESTION, 60, TimeUnit.MINUTES, 1),
    PING_PONG_BOTTLE_LIST(Name.PING_PONG_BOTTLE_LIST, 60, TimeUnit.MINUTES, 100),
    PING_PONG_BOTTLE(Name.PING_PONG_BOTTLE, 60, TimeUnit.MINUTES, 100),
    ;

    object Name {
        const val LETTER_QUESTION = "LETTER_QUESTION"
        const val PING_PONG_BOTTLE_LIST = "PING_PONG_BOTTLE_LIST"
        const val PING_PONG_BOTTLE = "PING_PONG_BOTTLE"
    }
}
