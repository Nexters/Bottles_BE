package com.nexters.bottles.admin.service

import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.domain.UserProfile
import com.nexters.bottles.user.repository.UserProfileRepository
import com.nexters.bottles.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
) {

    @Transactional
    fun saveMockUser(mockUser: User) {
        userRepository.save(mockUser)
    }

    @Transactional
    fun saveMockProfile(mockUserProfile: UserProfile) {
        userProfileRepository.save(mockUserProfile)
    }
}
