package com.nexters.bottles.api.global.interceptor

import com.nexters.bottles.api.global.exception.UnauthorizedException
import com.nexters.bottles.app.auth.service.JwtTokenProvider
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthInterceptor(private val jwtTokenProvider: JwtTokenProvider) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) {
            return true;
        }
        if (handler.hasMethodAnnotation(AuthRequired::class.java)) {
            val token = jwtTokenProvider.resolveToken(request)
            if (token == null || !jwtTokenProvider.validateToken(token, isAccessToken = true)) {
                throw UnauthorizedException("고객센터에 문의해주세요")
            }
        }
        return true
    }
}
