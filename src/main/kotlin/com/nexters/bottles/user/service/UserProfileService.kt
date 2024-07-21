package com.nexters.bottles.user.service

import com.nexters.bottles.user.domain.UserProfile
import com.nexters.bottles.user.repository.UserProfileRepository
import com.nexters.bottles.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserProfileService(
    private val profileRepository: UserProfileRepository,
    private val userRepository: UserRepository,
) {

    private val log = KotlinLogging.logger { }

    @Transactional
    fun saveProfile(userProfile: UserProfile): UserProfile {
        log.info { "test!: $userProfile" }
        val user = userRepository.findByIdOrNull(1L) // TODO User 회원 가입 기능 구현후 수정

        log.info { "test!!: $user" }
        userProfile.user = user
        return profileRepository.save(userProfile)
    }
}
