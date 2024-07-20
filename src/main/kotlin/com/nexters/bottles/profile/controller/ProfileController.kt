package com.nexters.bottles.profile.controller

import com.nexters.bottles.profile.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.profile.facade.ProfileFacade
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ProfileController(
    private val profileFacade: ProfileFacade,
) {

    @PostMapping("/profile/choice")
    fun registerProfile(@RequestBody registerProfileRequestDto: RegisterProfileRequestDto) {
        profileFacade.saveProfile(registerProfileRequestDto)
    }
}
