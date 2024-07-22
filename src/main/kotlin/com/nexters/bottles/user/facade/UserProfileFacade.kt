package com.nexters.bottles.user.facade

import com.nexters.bottles.user.controller.dto.ProfileChoiceResponseDto
import com.nexters.bottles.user.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.user.domain.UserProfile
import com.nexters.bottles.user.domain.UserProfileSelect
import com.nexters.bottles.user.service.UserProfileService
import org.springframework.stereotype.Component
import regions

@Component
class UserProfileFacade(
    private val profileService: UserProfileService,
) {

    fun saveProfile(profileDto: RegisterProfileRequestDto) {
        validateProfile(profileDto)
        val convertedProfileDto = convertProfileDto(profileDto)

        profileService.saveProfile(
            UserProfile(
                profileSelect = UserProfileSelect(
                    mbti = convertedProfileDto.mbti,
                    keyword = convertedProfileDto.keyword,
                    interest = convertedProfileDto.interest,
                    job = convertedProfileDto.job,
                    smoking = convertedProfileDto.smoking,
                    alcohol = convertedProfileDto.alcohol,
                    religion = convertedProfileDto.religion,
                    region = convertedProfileDto.region,
                )
            )
        )
    }

    fun getProfileChoice(): ProfileChoiceResponseDto {
        return ProfileChoiceResponseDto(
            regions = regions
        )
    }

    private fun validateProfile(profileDto: RegisterProfileRequestDto) {
        require(profileDto.keyword.size <= 5) {
            "키워드는 5개 이하여야 해요"
        }
        val interestCount = profileDto.interest.culture.size + profileDto.interest.sports.size
        +profileDto.interest.entertainment.size + profileDto.interest.etc.size
        require(interestCount <= 5) {
            "취미는 5개 이하여야 해요"
        }
    }

    private fun convertProfileDto(profileDto: RegisterProfileRequestDto): RegisterProfileRequestDto {
        when(profileDto.smoking) {
            "전혀 피우지 않아요" -> profileDto.smoking = "흡연 안해요"
            "가끔 피워요" -> profileDto.smoking = "흡연은 가끔"
            "자주 피워요" -> profileDto.smoking = "흡연해요"
        }
        when(profileDto.alcohol) {
            "한 방울도 마시지 않아요" -> profileDto.smoking = "술은 안해요"
            "때에 따라 적당히 즐겨요" -> profileDto.smoking = "술은 적당히"
            "자주 찾는 편이에요" -> profileDto.smoking = "술을 즐겨요"
        }
        return profileDto
    }
}
