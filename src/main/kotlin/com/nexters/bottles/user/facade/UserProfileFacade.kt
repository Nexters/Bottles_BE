package com.nexters.bottles.user.facade

import com.nexters.bottles.user.facade.dto.ProfileChoiceResponseDto
import com.nexters.bottles.user.facade.dto.RegisterIntroductionRequestDto
import com.nexters.bottles.user.facade.dto.RegisterProfileRequestDto
import com.nexters.bottles.user.domain.UserProfileSelect
import com.nexters.bottles.user.service.UserProfileService
import org.springframework.stereotype.Component
import regions

@Component
class UserProfileFacade(
    private val profileService: UserProfileService,
) {

    fun upsertProfile(profileDto: RegisterProfileRequestDto) {
        validateProfile(profileDto)
        val convertedProfileDto = convertProfileDto(profileDto)

        profileService.upsertProfile(
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

    private fun validateIntroduction(introductionDto: RegisterIntroductionRequestDto) {
        introductionDto.introduction.forEach {
//            require(it.answer.length > 30 && it.answer.length <= 100) {
//                "소개는 30자 이상 100자 이하로 써야 해요"
//            }
            // TODO: 개발 환경에서 빠르게 테스트 하기 위해 일단 주석 처리하고 라이브 서비스 나가기전 해제할 예정입니다.
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
