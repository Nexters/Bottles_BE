package com.nexters.bottles.api.bottle.controller

import com.nexters.bottles.api.bottle.facade.BottleFacadeV2
import com.nexters.bottles.api.bottle.facade.dto.PingPongListResponseV2
import com.nexters.bottles.api.bottle.facade.dto.RandomBottleListResponse
import com.nexters.bottles.api.bottle.facade.dto.SentBottleListResponse
import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/bottles")
class BottleControllerV2(
    private val bottleFacadeV2: BottleFacadeV2
) {

    @ApiOperation("모래사장 - 랜덤으로 받은 보틀 목록 조회하기")
    @GetMapping("/random")
    @AuthRequired
    fun getRandomBottlesList(@AuthUserId userId: Long): RandomBottleListResponse {
        return bottleFacadeV2.getRandomBottles(userId)
    }

    @ApiOperation("호감 - 호감을 받은 보틀 목록 조회하기")
    @GetMapping("/sent")
    @AuthRequired
    fun getSentBottlesList(@AuthUserId userId: Long): SentBottleListResponse {
        return bottleFacadeV2.getSentBottles(userId)
    }

    @ApiOperation("문답 - 핑퐁중인 보틀 목록 조회하기")
    @GetMapping("/ping-pong")
    @AuthRequired
    fun getPingPongList(@AuthUserId userId: Long): PingPongListResponseV2 {
        return bottleFacadeV2.getPingPongBottles(userId)
    }

    // TODO: 따닥 방지
    @ApiOperation("마이페이지 - 추가로 보틀 받기")
    @GetMapping("/additional-random")
    @AuthRequired
    fun getAdditionalRandomBottle(@AuthUserId userId: Long) {
        return bottleFacadeV2.getAdditionalRandomBottle(userId)
    }

    @ApiOperation("보틀 읽음 표시하기")
    @PostMapping("/bottle/{bottleId}/read")
    @AuthRequired
    fun readBottle(@AuthUserId userId: Long, @PathVariable bottleId: Long) {
        bottleFacadeV2.readBottle(userId, bottleId)
    }
}
