package com.nexters.bottles.app.admin.service

import com.nexters.bottles.app.auth.domain.BlackList
import com.nexters.bottles.app.auth.domain.enum.TokenType
import com.nexters.bottles.app.auth.repository.BlackListRepository
import com.nexters.bottles.app.auth.repository.RefreshTokenRepository
import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.vo.LikeMessage
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.domain.UserProfile
import com.nexters.bottles.app.user.repository.UserProfileRepository
import com.nexters.bottles.app.user.repository.UserRepository
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

    @TestOnly
    @Transactional
    fun expireAccessToken(token: String) {
        val blackList = BlackList(expiredAccessToken = token)
        blackListRepository.save(blackList)
    }

    @TestOnly
    @Transactional
    fun expireRefreshToken(token: String, userId: Long) {
        blackListRepository.save(
            BlackList(
                expiredAccessToken = token,
                tokenType = TokenType.REFRESH_TOKEN
            )
        )
    }

    @TestOnly
    @Transactional
    fun cleanUpMockUpData(user: User) {
        userRepository.findByIdOrNull(user.id)?.let { user ->
            letterRepository.findAllByUserId(user.id).forEach {
                letterRepository.deleteById(it.id)
            }
            bottleRepository.findAllByTargetUser(user).forEach {
                bottleRepository.deleteById(it.id)
            }
            bottleRepository.findAllBySourceUser(user).forEach {
                bottleRepository.deleteById(it.id)
            }
            refreshTokenRepository.findAllByUserId(user.id).forEach {
                refreshTokenRepository.deleteById(it.id)
            }
        }
    }

    @TestOnly
    @Transactional
    fun forceBottleReceive(mockMaleUser: User, mockFemaleUser: User, bottleStatus: BottleStatus, likeMessage: String?) {
        bottleRepository.save(
            Bottle(
                targetUser = mockMaleUser,
                sourceUser = mockFemaleUser,
                bottleStatus = bottleStatus,
                likeMessage = if (likeMessage == null) null else LikeMessage(likeMessage),
            )
        )
    }
}
