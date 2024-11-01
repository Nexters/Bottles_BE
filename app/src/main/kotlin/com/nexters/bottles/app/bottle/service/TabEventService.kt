package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.service.dto.TabEventDto
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


@Service
class TabEventService {

    private val sseEmitters: MutableMap<Long, SseEmitter> = ConcurrentHashMap()

    fun connectSse(userId: Long): SseEmitter {
        val sseEmitter = SseEmitter(300_000L)
        val sseEventBuilder = SseEmitter.event()
            .name("tab")
            .data("connected")
            .reconnectTime(10000L)

        sendEvent(sseEmitter, sseEventBuilder)

        sseEmitters[userId] = sseEmitter

        sseEmitter.onCompletion { sseEmitters.remove(userId) }
        sseEmitter.onTimeout { sseEmitter.complete() }

        return sseEmitter
    }

    fun sendEventByTabType(to: Long, event: TabEventDto) {
        val sseEventBuilder = SseEmitter.event()
            .name("tab")
            .data(event)
            .reconnectTime(10000L)

        val userSseEmitter = sseEmitters.getOrDefault(to, null)
        if (userSseEmitter != null) {
            sendEvent(userSseEmitter, sseEventBuilder)
        }
    }

    private fun sendEvent(sseEmitter: SseEmitter, sseEventBuilder: SseEventBuilder) {
        try {
            sseEmitter.send(sseEventBuilder)
        } catch (e: IOException) {
            sseEmitter.complete()
        }
    }
}
