package com.nexters.bottles.app.user.service

import com.nexters.bottles.app.user.component.event.dto.IntroductionSaveEventDto
import com.nexters.bottles.app.user.component.event.dto.UploadImageEventDto
import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.domain.UserProfileSelect
import com.nexters.bottles.app.user.repository.UserProfileRepository
import com.nexters.bottles.app.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileService(
    private val profileRepository: UserProfileRepository,
    private val userRepository: UserRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log = KotlinLogging.logger { }

    @Transactional
    fun upsertProfile(userId: Long, profileSelect: UserProfileSelect) {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        profileRepository.findByUserId(user.id)?.let {
            it.profileSelect = profileSelect
        } ?: run {
            profileRepository.save(
                UserProfile(
                    user = user,
                    profileSelect = profileSelect,
                )
            )
        }
    }

    @Transactional
    fun saveIntroduction(userId: Long, introduction: List<QuestionAndAnswer>, firstMatchingCount: Int) {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        profileRepository.findByUserId(user.id)?.let {
            val isFirstRegisterIntroduction = it.introduction.isEmpty()
            it.introduction = introduction
            if (isFirstRegisterIntroduction) {
                repeat(firstMatchingCount) {
                    applicationEventPublisher.publishEvent(
                        IntroductionSaveEventDto(userId = userId)
                    )
                }
            }
        } ?: run {
            profileRepository.save(
                UserProfile(
                    user = user,
                    introduction = introduction
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun findUserProfile(userId: Long): UserProfile? {
        return profileRepository.findByUserId(userId)
    }

    @Transactional
    fun uploadImageUrl(user: User, imageUrl: String, blurredImageUrl: String) {
        profileRepository.findByUserId(user.id)?.let {
            it.imageUrl = imageUrl
            it.imageUrls = listOf(imageUrl)
            it.blurredImageUrl = blurredImageUrl
        } ?: throw IllegalArgumentException("고객센터에 문의해주세요")
    }

    @Transactional
    fun upsertImageUrls(userId: Long, imageUrls: List<String>, blurredImageUrl: String) {
        profileRepository.findByUserId(userId)?.let {
            val uploadImageEventDto = UploadImageEventDto(
                prevImageUrls = it.imageUrls,
                prevBlurredImageUrl = it.blurredImageUrl
            )
            it.imageUrl = imageUrls[0]
            it.imageUrls = imageUrls
            it.blurredImageUrl = blurredImageUrl
            applicationEventPublisher.publishEvent(uploadImageEventDto)
        } ?: throw IllegalArgumentException("고객센터에 문의해주세요")
    }

    @Transactional
    fun deleteUserProfile(userId: Long) {
        profileRepository.findByUserId(userId)?.let { profile ->
            profileRepository.deleteById(profile.id)
        }
    }

    @Transactional(readOnly = true)
    fun findAllWithImage(): List<UserProfile> {
        return profileRepository.findAllWithUser().filter { it.imageUrl != null }
    }

    @Transactional
    fun addBlurImageUrl(id: Long, blurredImageUrl: String) {
        profileRepository.findByIdOrNull(id)?.let {
            it.blurredImageUrl = blurredImageUrl
        }
    }
}
