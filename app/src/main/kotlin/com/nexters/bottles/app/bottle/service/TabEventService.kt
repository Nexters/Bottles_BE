package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.enum.TabType
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


@Service
class TabEventService {

    private val sseEmitters: MutableMap<Long, MutableMap<String, SseEmitter>> = ConcurrentHashMap()

    fun connect(userId: Long, tapType: TabType): SseEmitter {
        val sseEmitter = SseEmitter(300_000L)
        val sseEventBuilder = SseEmitter.event()
            .name(tapType.name)
            .data("connected")
            .reconnectTime(10000L)

        sendEvent(sseEmitter, sseEventBuilder)

        val userSseEmitters = sseEmitters.getOrDefault(userId, ConcurrentHashMap())
        userSseEmitters[tapType.name] = sseEmitter
        sseEmitters[userId] = userSseEmitters

        sseEmitter.onCompletion { userSseEmitters.remove(tapType.name) }
        sseEmitter.onTimeout { sseEmitter.complete() }

        return sseEmitter
    }

    fun sendEventByTabType(to: Long, tabType: TabType) {
        val userSseEmitters = sseEmitters.getOrDefault(to, ConcurrentHashMap())

        val sseEventBuilder = SseEmitter.event()
            .name(tabType.name)
            .data("new")
            .reconnectTime(10000L)

        val userSseEmitter = userSseEmitters.getOrDefault(tabType.name, null)
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
