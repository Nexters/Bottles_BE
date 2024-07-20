package com.nexters.bottles.profile.service

import com.nexters.bottles.profile.controller.dto.RegisterProfileRequestDto
import com.nexters.bottles.profile.domain.UserProfile
import com.nexters.bottles.profile.repository.UserProfileRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ProfileService(
    private val profileRepository: UserProfileRepository
) {

    @Transactional
    fun saveProfile(userProfile: UserProfile): UserProfile {
        return profileRepository.save(userProfile)
    }
}