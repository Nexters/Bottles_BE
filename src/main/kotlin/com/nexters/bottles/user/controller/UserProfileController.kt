package com.nexters.bottles.user.controller

import com.nexters.bottles.user.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.user.facade.UserProfileFacade
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserProfileController(
    private val profileFacade: UserProfileFacade,
) {

    @PostMapping("/profile/choice")
    fun registerProfile(@RequestBody registerProfileRequestDto: RegisterProfileRequestDto) {
        profileFacade.saveProfile(registerProfileRequestDto)
    }
}
