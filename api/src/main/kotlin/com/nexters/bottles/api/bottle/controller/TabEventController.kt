package com.nexters.bottles.api.bottle.controller

import com.nexters.bottles.api.bottle.facade.dto.TabTypeRequest
import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.app.bottle.service.TabEventService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class TabEventController(
    private val tabEventService: TabEventService
) {

    @GetMapping("/connect", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @AuthRequired
    fun connect(@AuthUserId userId: Long, @ModelAttribute("tapType") tapType: TabTypeRequest): SseEmitter {
        val sseEmitter = tabEventService.connect(userId, tapType.tabType)
        return sseEmitter
    }
}
