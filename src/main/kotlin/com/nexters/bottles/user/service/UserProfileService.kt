package com.nexters.bottles.user.service

import com.nexters.bottles.user.domain.QuestionAndAnswer
import com.nexters.bottles.user.domain.UserProfile
import com.nexters.bottles.user.domain.UserProfileSelect
import com.nexters.bottles.user.repository.UserProfileRepository
import com.nexters.bottles.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileService(
    private val profileRepository: UserProfileRepository,
    private val userRepository: UserRepository,
) {

    private val log = KotlinLogging.logger { }

    @Transactional
    fun upsertProfile(userId: Long, profileSelect: UserProfileSelect) {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        profileRepository.findByUserId(user.id)?.let {
            it.user = user
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
    fun saveIntroduction(userId: Long, introduction: List<QuestionAndAnswer>) {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        profileRepository.findByUserId(user.id)?.let {
            it.introduction = introduction
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
}
