package com.nexters.bottles.config

import com.nexters.bottles.global.interceptor.AuthInterceptor
import com.nexters.bottles.global.interceptor.RefreshAuthInterceptor
import com.nexters.bottles.global.resolver.AccessTokenArgumentResolver
import com.nexters.bottles.global.resolver.AuthUserIdArgumentResolver
import com.nexters.bottles.global.resolver.RefreshTokenArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authInterceptor: AuthInterceptor,
    private val refreshAuthInterceptor: RefreshAuthInterceptor,
    private val authUserIdArgumentResolver: AuthUserIdArgumentResolver,
    private val refreshAuthUserIdArgumentResolver: RefreshTokenArgumentResolver,
    private val accessTokenArgumentResolver: AccessTokenArgumentResolver,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
        registry.addInterceptor(refreshAuthInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authUserIdArgumentResolver)
        resolvers.add(refreshAuthUserIdArgumentResolver)
        resolvers.add(accessTokenArgumentResolver)
    }
}
