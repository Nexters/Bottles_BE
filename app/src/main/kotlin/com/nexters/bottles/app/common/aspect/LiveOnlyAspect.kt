package com.nexters.bottles.app.common.aspect

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Aspect
@Component
class LiveOnlyAspect(
    private val environment: Environment,
) {

    private val log = KotlinLogging.logger {}

    @Around("@annotation(com.nexters.bottles.app.common.annotation.LiveOnly)")
    fun devOnlyMethod(joinPoint: ProceedingJoinPoint): Any? {
        return if (environment.activeProfiles.any { it == "dev" || it == "live" }) {
            joinPoint.proceed()
        } else {
            log.info { "LiveOnly여서 로컬에서 실행되지 않음" }
            null
        }
    }
}
