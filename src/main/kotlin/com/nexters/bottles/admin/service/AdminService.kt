package com.nexters.bottles.admin.service

import com.nexters.bottles.auth.domain.BlackList
import com.nexters.bottles.auth.repository.BlackListRepository
import com.nexters.bottles.auth.repository.RefreshTokenRepository
import com.nexters.bottles.bottle.domain.Bottle
import com.nexters.bottles.bottle.repository.BottleRepository
import com.nexters.bottles.bottle.repository.LetterRepository
import com.nexters.bottles.user.domain.User
import com.nexters.bottles.user.domain.UserProfile
import com.nexters.bottles.user.repository.UserProfileRepository
import com.nexters.bottles.user.repository.UserRepository
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
        userRepository.findByIdOrNull(user.id)?.let {user ->
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
