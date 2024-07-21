package com.nexters.bottles.user.controller

import com.nexters.bottles.user.controller.dto.ProfileChoiceResponseDto
import com.nexters.bottles.user.controller.dto.ProfileResponseDto
import com.nexters.bottles.user.controller.dto.RegisterIntroductionRequestDto
import com.nexters.bottles.user.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.user.facade.UserProfileFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profile")
class UserProfileController(
    private val profileFacade: UserProfileFacade,
) {

    @PostMapping("/choice")
    fun upsertProfile(@RequestBody registerProfileRequestDto: RegisterProfileRequestDto) {
        profileFacade.upsertProfile(registerProfileRequestDto)
    }

    @GetMapping("/choice")
    fun getProfileChoiceList() : ProfileChoiceResponseDto {
        return profileFacade.getProfileChoice()
    }

    @PostMapping("/introduction")
    fun upsertIntroduction(@RequestBody registerIntroductionRequestDto: RegisterIntroductionRequestDto) {
        profileFacade.upsertIntroduction(registerIntroductionRequestDto)
    }

    @GetMapping
    fun getProfile(): ProfileResponseDto {
        return profileFacade.getProfile()
    }
}
