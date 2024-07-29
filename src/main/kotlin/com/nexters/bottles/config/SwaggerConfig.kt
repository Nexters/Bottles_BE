package com.nexters.bottles.config

import com.nexters.bottles.global.interceptor.AuthRequired
import com.nexters.bottles.global.resolver.AuthUserId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.nexters.bottles"))
            .paths(PathSelectors.any())
            .build()
            .ignoredParameterTypes(AuthUserId::class.java)
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(ApiKey("Authorization", "Authorization", "header")))
    }

    private fun securityContext(): SecurityContext? =
        SecurityContext.builder()
            .securityReferences(defaultAuth())
            .operationSelector { operationContext ->
                operationContext.requestMappingPattern().let { _ ->
                    val handlerMethod = operationContext.findAnnotation(AuthRequired::class.java)
                    !handlerMethod.isEmpty
                }
            }
            .build()

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOf(authorizationScope)
        return listOf(SecurityReference("Authorization", authorizationScopes))
    }
}
