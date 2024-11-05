package com.nexters.bottles.api.bottle.controller

import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.app.bottle.service.TabEventService
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/tab")
class TabEventController(
    private val tabEventService: TabEventService
) {

    @ApiOperation("탭바 SSE 연결 - New 뱃지 여부 이벤트")
    @GetMapping("/connect/sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @AuthRequired
    fun connectSse(@AuthUserId userId: Long): SseEmitter {
        val sseEmitter = tabEventService.connectSse(userId)
        return sseEmitter
    }
}
