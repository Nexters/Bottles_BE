package com.nexters.bottles.bottle.controller

import com.nexters.bottles.bottle.facade.BottleFacade
import com.nexters.bottles.bottle.facade.dto.BottleDetailResponseDto
import com.nexters.bottles.bottle.facade.dto.BottleListResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bottles")
class BottleController(
    private val bottleFacade: BottleFacade
) {

    @GetMapping
    fun getBottlesList(): BottleListResponseDto {
        return bottleFacade.getBottles()
    }

    @GetMapping("/{bottleId}")
    fun getBottleDetail(@PathVariable bottleId: Long): BottleDetailResponseDto {
        return bottleFacade.getBottle(bottleId)
    }

    @PostMapping("/{bottleId}/accept")
    fun acceptBottle(@PathVariable bottleId: Long) {
        bottleFacade.acceptBottle(bottleId)
    }

    @PostMapping("{bottleId}/refuse")
    fun refuseBottle(@PathVariable bottleId: Long) {
        bottleFacade.refuseBottle(bottleId)
    }
}
