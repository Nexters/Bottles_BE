package com.nexters.bottles.api.user.controller

import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.api.user.facade.UserFacade
import com.nexters.bottles.api.user.facade.dto.*
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
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

    @ApiOperation("연락처 차단 목록 등록")
    @PostMapping("/block/contact-list")
    @AuthRequired
    fun blockContactList(@AuthUserId userId: Long, @RequestBody blockContactListRequest: BlockContactListRequest) {
        userFacade.blockContactList(userId, blockContactListRequest.blockContacts)
    }

    @ApiOperation("알림 설정")
    @PostMapping("/alimy")
    @AuthRequired
    fun turnOnOffAlimy(@AuthUserId userId: Long, @RequestBody alimyOnOffRequest: AlimyOnOffRequest) {
        userFacade.turnOnOffAlimy(userId, alimyOnOffRequest)
    }

    @ApiOperation("알림 설정 조회")
    @GetMapping("/alimy")
    @AuthRequired
    fun turnOnOffAlimy(@AuthUserId userId: Long): List<AlimyResponse> {
        return userFacade.getAlimy(userId)
    }

    @ApiOperation("폰 알림 허용 유무 등록")
    @PostMapping("/alimy/native")
    @AuthRequired
    fun registerNativeAlimy(@AuthUserId userId: Long, @RequestBody nativeAlimyRequest: NativeAlimyRequest) {
        return userFacade.registerNativeAlimyStatus(userId, nativeAlimyRequest)
    }
}
