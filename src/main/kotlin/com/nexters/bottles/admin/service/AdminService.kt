package com.nexters.bottles.admin.service

import com.nexters.bottles.auth.domain.BlackList
import com.nexters.bottles.auth.repository.BlackListRepository
import com.nexters.bottles.auth.repository.RefreshTokenRepository
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
    private val blackListRepository: BlackListRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @Transactional
    fun saveMockUser(mockUser: User) {
        userRepository.save(mockUser)
    }

    @Transactional
    fun saveMockProfile(mockUserProfile: UserProfile) {
        userProfileRepository.save(mockUserProfile)
    }

    @Transactional
    fun expireAccessToken(token: String) {
        val blackList = BlackList(expiredAccessToken = token)
        blackListRepository.save(blackList)
    }

    @Transactional
    fun expireRefreshToken(token: String, userId: Long) {
        refreshTokenRepository.findAllByUserId(userId)
            .forEach { refreshTokenRepository.deleteById(it.id) }
    }
}
