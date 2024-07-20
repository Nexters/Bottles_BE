package com.nexters.bottles.profile.facade

import com.nexters.bottles.profile.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.profile.domain.ProfileSelect
import com.nexters.bottles.profile.domain.UserProfile
import com.nexters.bottles.profile.service.ProfileService
import org.springframework.stereotype.Component

@Component
class ProfileFacade(
    private val profileService: ProfileService,
) {

    fun saveProfile(profileDto: RegisterProfileRequestDto) {
        validateProfile(profileDto)

        profileService.saveProfile(
            UserProfile(
                userId = 1L,
                profileSelect = ProfileSelect(
                    mbti = profileDto.mbti,
                    keyword = profileDto.keyword,
                    interest = profileDto.interest,
                    job = profileDto.job,
                    smoking = profileDto.smoking,
                    alcohol = profileDto.alcohol,
                    religion = profileDto.religion,
                    region = profileDto.region,
                )
            )
        )
    }

    private fun validateProfile(profileDto: RegisterProfileRequestDto) {
        require(profileDto.keyword.size <= 5) {
            "키워드는 5개 이하여야 해요"
        }
        val interestCount = profileDto.interest.culture.size + profileDto.interest.sports.size
            + profileDto.interest.entertainment.size + profileDto.interest.etc.size
        require(interestCount <= 5) {
            "취미는 5개 이하여야 해요"
        }
    }
}