package com.nexters.bottles.api.admin.service

import com.nexters.bottles.api.auth.domain.BlackList
import com.nexters.bottles.api.auth.repository.BlackListRepository
import com.nexters.bottles.api.auth.repository.RefreshTokenRepository
import com.nexters.bottles.api.bottle.domain.Bottle
import com.nexters.bottles.api.bottle.repository.BottleRepository
import com.nexters.bottles.api.bottle.repository.LetterRepository
import com.nexters.bottles.api.user.domain.User
import com.nexters.bottles.api.user.domain.UserProfile
import com.nexters.bottles.api.user.repository.UserProfileRepository
import com.nexters.bottles.api.user.repository.UserRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val bottleRepository: BottleRepository,
    private val letterRepository: LetterRepository,
    private val blackListRepository: BlackListRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @TestOnly
    @Transactional
    fun saveMockUser(mockUser: User) {
        userRepository.save(mockUser)
    }

    @TestOnly
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

    @TestOnly
    @Transactional
    fun cleanUpMockUpData(user: User) {
        userRepository.findByIdOrNull(user.id)?.let { user ->
            bottleRepository.findAllByTargetUser(user).forEach {
                bottleRepository.deleteById(it.id)
            }
            bottleRepository.findAllBySourceUser(user).forEach {
                bottleRepository.deleteById(it.id)
            }
            letterRepository.findAllByUserId(user.id).forEach {
                letterRepository.deleteById(it.id)
            }
            refreshTokenRepository.findAllByUserId(user.id).forEach {
                refreshTokenRepository.deleteById(it.id)
            }
        }
    }

    @TestOnly
    @Transactional
    fun forceBottleReceive(mockMaleUser: User, mockFemaleUser: User) {
        bottleRepository.save(
            Bottle(
                targetUser = mockFemaleUser,
                sourceUser = mockMaleUser,
            )
        )
    }
}
