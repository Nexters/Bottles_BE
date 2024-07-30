package com.nexters.bottles.bottle.controller

import com.nexters.bottles.bottle.facade.BottleFacade
import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleImageShareRequest
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleMatchRequest
import com.nexters.bottles.bottle.facade.dto.BottlePingpongResponseDto
import com.nexters.bottles.bottle.facade.dto.PingPongListResponseDto
import com.nexters.bottles.bottle.facade.dto.RegisterLetterRequestDto
import com.nexters.bottles.global.interceptor.AuthRequired
import com.nexters.bottles.global.resolver.AuthUserId
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bottles")
class BottleController(
    private val bottleFacade: BottleFacade
) {

    @ApiOperation("홈 - 받은 보틀 목록 조회하기")
    @GetMapping
    @AuthRequired
    fun getBottlesList(@AuthUserId userId: Long): BottleListResponseDto {
        return bottleFacade.getNewBottles(userId)
    }

    @ApiOperation("홈 - 보틀 상세 정보 조회하기")
    @GetMapping("/{bottleId}")
    @AuthRequired
    fun getBottleDetail(@PathVariable bottleId: Long): BottleDetailResponseDto {
        return bottleFacade.getBottle(bottleId)
    }

    @ApiOperation("홈 - 보틀에 내 소개 보내기(수락하기)")
    @PostMapping("/{bottleId}/accept")
    @AuthRequired
    fun acceptBottle(@AuthUserId userId: Long, @PathVariable bottleId: Long) {
        bottleFacade.acceptBottle(userId, bottleId)
    }

    @ApiOperation("홈 - 보틀 떠내려 보내기(거절하기)")
    @PostMapping("/{bottleId}/refuse")
    @AuthRequired
    fun refuseBottle(@AuthUserId userId: Long, @PathVariable bottleId: Long) {
        bottleFacade.refuseBottle(userId, bottleId)
    }

    @ApiOperation("보틀 보관함 - 보틀 보관함 조회하기")
    @GetMapping("/ping-pong")
    @AuthRequired
    fun getPingPongList(@AuthUserId userId: Long): PingPongListResponseDto {
        return bottleFacade.getPingPongBottles(userId)
    }

    @ApiOperation("보틀 보관함 - 편지 답변 등록하기")
    @PostMapping("/ping-pong/{bottleId}/letters")
    @AuthRequired
    fun registerLetter(
        @AuthUserId userId: Long,
        @PathVariable bottleId: Long,
        @RequestBody registerLetterRequestDto: RegisterLetterRequestDto
    ) {
        bottleFacade.registerLetter(userId, bottleId, registerLetterRequestDto)
    }

    @ApiOperation("보틀 보관함 - 보틀 읽음 표시하기")
    @PostMapping("/ping-pong/{bottleId}/read")
    @AuthRequired
    fun readPingPongBottle(@AuthUserId userId: Long, @PathVariable bottleId: Long) {
        bottleFacade.readPingPongBottle(userId, bottleId)
    }

    @ApiOperation("보틀 보관함 - 대화 중단하기")
    @PostMapping("/ping-pong/{bottleId}/stop")
    @AuthRequired
    fun stopBottle(@AuthUserId userId: Long, @PathVariable bottleId: Long) {
        bottleFacade.stopBottle(userId, bottleId)
    }

    @ApiOperation("보틀의 핑퐁 조회하기")
    @GetMapping("/ping-pong/{bottleId}")
    @AuthRequired
    fun getBottlePingPong(@AuthUserId userId: Long, @PathVariable bottleId: Long): BottlePingpongResponseDto {
        return bottleFacade.getBottlePingPong(userId, bottleId)
    }

    @ApiOperation("사진 공유 선택하기")
    @PostMapping("/ping-pong/{bottleId}/image")
    @AuthRequired
    fun selectShareImage(
        @AuthUserId userId: Long,
        @PathVariable bottleId: Long,
        @RequestBody bottleImageShareRequest: BottleImageShareRequest
    ) {
        bottleFacade.selectShareImage(userId, bottleId, bottleImageShareRequest.willShare)
    }

    @ApiOperation("최종 선택하기")
    @PostMapping("/ping-pong/{bottleId}/match")
    @AuthRequired
    fun selectMatch(
        @AuthUserId userId: Long,
        @PathVariable bottleId: Long,
        @RequestBody bottleMatchRequest: BottleMatchRequest
    ) {
        bottleFacade.selectMatch(userId, bottleId, bottleMatchRequest.willMatch)
    }
}
