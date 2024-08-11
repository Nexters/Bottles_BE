package com.nexters.bottles.api.user.controller

import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.api.user.facade.UserFacade
import com.nexters.bottles.api.user.facade.dto.RegisterProfileRequest
import com.nexters.bottles.api.user.facade.dto.ReportUserRequest
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userFacade: UserFacade,
) {

    @ApiOperation("유저 신고/차단하기")
    @PostMapping("/report")
    @AuthRequired
    fun reportUser(@AuthUserId userId: Long, @RequestBody reportUserRequest: ReportUserRequest) {
        userFacade.reportUser(userId, reportUserRequest)
    }
}
