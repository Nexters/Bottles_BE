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

    @Transactional(readOnly = true)
    fun findAllowedUserAlimyByUserIdsAndAlimyType(userIds: Set<Long>, alimyType: AlimyType): List<UserAlimy> {
        return userAlimyRepository.findAllByUserIds(userIds)
            .filter { it.alimyType == alimyType }
            .filter { it.enabled }
    }

    @Transactional(readOnly = true)
    fun isTurnedOn(id: Long, alimyType: AlimyType): Boolean {
        return userAlimyRepository.findByUserIdAndAlimyType(id, alimyType)?.enabled ?: false
    }
}
