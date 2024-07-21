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
    fun upsertProfile(profileSelect: UserProfileSelect) {
        // TODO User 회원 가입 기능 구현후 수정
        val user = userRepository.findByIdOrNull(1L)
        if (user != null) {
            user.userProfile?.profileSelect = profileSelect

            profileRepository.findByUserId(user.id)?.let {
                it.user = user
                it.profileSelect = it.profileSelect
                it.introduction = it.introduction
            } ?: run {
                profileRepository.save(
                    UserProfile(
                        user = user,
                        profileSelect = profileSelect,
                    )
                )
            }
        } else {
            throw IllegalStateException ("회원가입 상태를 문의해주세요")
        }
    }

    @Transactional
    fun saveIntroduction(introduction: List<QuestionAndAnswer>) {
        // TODO User 회원 가입 기능 구현후 수정
        val user = userRepository.findByIdOrNull(1L)
        if (user != null) {
            user.userProfile?.introduction = introduction

            profileRepository.findByUserId(user.id)?.let {
                it.user = user
                it.profileSelect = it.profileSelect
                it.introduction = it.introduction
            } ?: run {
                profileRepository.save(
                    UserProfile(
                        user = user,
                        introduction = introduction
                    )
                )
            }
        } else {
            throw IllegalStateException ("회원가입 상태를 문의해주세요")
        }
    }

    @Transactional(readOnly = true)
    fun findProfile() : UserProfile? {
        val user = userRepository.findByIdOrNull(1L) ?: return null
        return profileRepository.findByUserId(user.id)
    }
}
