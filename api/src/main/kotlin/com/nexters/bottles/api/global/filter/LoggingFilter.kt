package com.nexters.bottles.api.global.filter

import com.nexters.bottles.api.auth.component.JwtTokenProvider
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggingFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        MDC.clear()
        try {
            val token = jwtTokenProvider.resolveToken(request)
            if (token != null) {
                val userId = jwtTokenProvider.getUserIdFromToken(token, isAccessToken = true)
                MDC.put("userId", userId?.toString() ?: "anonymous")
            } else {
                MDC.put("userId", "anonymous")
            }

            log.info("Request: method: ${request.method} ${request.requestURI} userId: ${MDC.get("userId")}")
            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }
}
