package com.nexters.bottles.user.facade

import com.nexters.bottles.user.controller.dto.ProfileChoiceResponseDto
import com.nexters.bottles.user.controller.dto.ProfileResponseDto
import com.nexters.bottles.user.controller.dto.RegisterIntroductionRequestDto
import com.nexters.bottles.user.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.user.domain.UserProfileSelect
import com.nexters.bottles.user.service.UserProfileService
import mu.KotlinLogging
import org.springframework.stereotype.Component
import regions

@Component
class UserProfileFacade(
    private val profileService: UserProfileService,
) {

    private val log = KotlinLogging.logger {  }

    fun upsertProfile(profileDto: RegisterProfileRequestDto) {
        validateProfile(profileDto)

        profileService.upsertProfile(
            profileSelect = UserProfileSelect(
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
    }

    fun getProfileChoice(): ProfileChoiceResponseDto {
        return ProfileChoiceResponseDto(
            regions = regions
        )
    }

    fun upsertIntroduction(registerIntroductionRequestDto: RegisterIntroductionRequestDto) {
        validateIntroduction(registerIntroductionRequestDto)

        profileService.saveIntroduction(registerIntroductionRequestDto.introduction)
    }

    fun getProfile() : ProfileResponseDto {
        val userProfile = profileService.findProfile() ?: return ProfileResponseDto.ofEmpty()
        return ProfileResponseDto(
            profileSelect = userProfile.profileSelect,
            introduction = userProfile.introduction,
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

    private fun validateIntroduction(introductionDto: RegisterIntroductionRequestDto) {
        introductionDto.introduction.forEach {
//            require(it.answer.length > 30 && it.answer.length <= 100) {
//                "소개는 30자 이상 100자 이하로 써야 해요"
//            }
            // TODO: 개발 환경에서 빠르게 테스트 하기 위해 일단 주석 처리하고 라이브 서비스 나가기전 해제할 예정입니다.
        }
    }
}
