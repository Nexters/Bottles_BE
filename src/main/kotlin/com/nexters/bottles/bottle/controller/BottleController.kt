package com.nexters.bottles.bottle.controller

import com.nexters.bottles.bottle.facade.BottleFacade
import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import com.nexters.bottles.bottle.facade.dto.BottlePingpongResponseDto
import com.nexters.bottles.bottle.facade.dto.PingPongListResponseDto
import com.nexters.bottles.bottle.facade.dto.RegisterLetterRequestDto
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/bottles")
class BottleController(
    private val bottleFacade: BottleFacade
) {

    @ApiOperation("홈 - 받은 보틀 목록 조회하기")
    @GetMapping
    fun getBottlesList(): BottleListResponseDto {
        return bottleFacade.getNewBottles()
    }

    @ApiOperation("홈 - 보틀 상세 정보 조회하기")
    @GetMapping("/{bottleId}")
    fun getBottleDetail(@PathVariable bottleId: Long): BottleDetailResponseDto {
        return bottleFacade.getBottle(bottleId)
    }

    @ApiOperation("홈 - 보틀에 내 소개 보내기(수락하기)")
    @PostMapping("/{bottleId}/accept")
    fun acceptBottle(@PathVariable bottleId: Long) {
        bottleFacade.acceptBottle(bottleId)
    }

    @ApiOperation("홈 - 보틀 떠내려 보내기(거절하기)")
    @PostMapping("/{bottleId}/refuse")
    fun refuseBottle(@PathVariable bottleId: Long) {
        bottleFacade.refuseBottle(bottleId)
    }

    @ApiOperation("보틀 보관함 - 보틀 보관함 조회하기")
    @GetMapping("/ping-pong")
    fun getPingPongList(): PingPongListResponseDto {
        return bottleFacade.getPingPongBottles()
    }

    @ApiOperation("보틀 보관함 - 편지 답변 등록하기")
    @PostMapping("/ping-pong/{bottleId}/letters")
    fun registerLetter(@PathVariable bottleId: Long, @RequestBody registerLetterRequestDto: RegisterLetterRequestDto) {
        bottleFacade.registerLetter(bottleId, registerLetterRequestDto)
    }

    @ApiOperation("보틀 보관함 - 보틀 읽음 표시하기")
    @PostMapping("/ping-pong/{bottleId}/read")
    fun readPingPongBottle(@PathVariable bottleId: Long) {
        bottleFacade.readPingPongBottle(bottleId)
    }

    @ApiOperation("보틀 보관함 - 대화 중단하기")
    @PostMapping("/ping-pong/{bottleId}/stop")
    fun stopBottle(@PathVariable bottleId: Long) {
        bottleFacade.stopBottle(bottleId)
    }

    @ApiOperation("보틀의 핑퐁 조회하기")
    @GetMapping("/ping-pong/{bottleId}")
    fun getBottlePingPong(@PathVariable bottleId: Long): BottlePingpongResponseDto {
        return bottleFacade.getBottlePingPong(bottleId)
    }

    @ApiOperation("보틀 보관함 - 사진 전송하기")
    @PostMapping("/ping-pong/{bottleId}/images")
    fun uploadImage(@PathVariable bottleId: Long, @RequestPart file: MultipartFile) {
        bottleFacade.uploadImage(bottleId, file)
    }
}
