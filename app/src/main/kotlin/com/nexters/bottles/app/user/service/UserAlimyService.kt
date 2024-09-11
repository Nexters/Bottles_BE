package com.nexters.bottles.app.user.service

import com.nexters.bottles.app.user.domain.UserAlimy
import com.nexters.bottles.app.user.domain.enum.AlimyType
import com.nexters.bottles.app.user.repository.UserAlimyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAlimyService(
    private val userAlimyRepository: UserAlimyRepository,
) {
    @Transactional
    fun turnOnOffAlimy(userId: Long, alimyType: AlimyType, enabled: Boolean) {
        userAlimyRepository.findByUserIdAndAlimyType(userId, alimyType)?.let {
            it.enabled = enabled
        } ?: run {
            userAlimyRepository.save(
                UserAlimy(userId = userId, alimyType = alimyType, enabled = enabled)
            )
        }
    }

    @Transactional(readOnly = true)
    fun findAlimies(userId: Long): List<UserAlimy> {
        return userAlimyRepository.findAllByUserId(userId)
    }
}
