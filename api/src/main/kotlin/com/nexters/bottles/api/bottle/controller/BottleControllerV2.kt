package com.nexters.bottles.api.bottle.controller

import com.nexters.bottles.api.bottle.facade.BottleFacadeV2
import com.nexters.bottles.api.bottle.facade.dto.RandomBottleListResponse
import com.nexters.bottles.api.bottle.facade.dto.SentBottleListResponse
import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
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
}
