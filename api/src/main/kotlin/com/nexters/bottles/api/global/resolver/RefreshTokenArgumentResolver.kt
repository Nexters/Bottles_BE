package com.nexters.bottles.api.global.resolver

import com.nexters.bottles.api.global.exception.UnauthorizedException
import com.nexters.bottles.app.auth.service.JwtTokenProvider
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletRequest

@Component
class RefreshTokenArgumentResolver(
    private val jwtTokenProvider: JwtTokenProvider
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(RefreshTokenUserId::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        val token = jwtTokenProvider.resolveToken(request)
        val userId = token?.takeIf { jwtTokenProvider.validateToken(it, isAccessToken = false) }
            ?.let { jwtTokenProvider.getUserIdFromToken(it, isAccessToken = false) }
        return userId ?: throw UnauthorizedException("고객센터에 문의해주세요")
    }
}
