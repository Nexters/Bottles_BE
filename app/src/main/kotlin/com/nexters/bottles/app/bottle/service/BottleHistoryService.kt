package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.BottleHistory
import com.nexters.bottles.app.bottle.repository.BottleHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BottleHistoryService(
    private val bottleHistoryRepository: BottleHistoryRepository
) {

    @Transactional
    fun saveMatchingHistory(sourceUserId: Long, targetUserId: Long) {
        val targetUserBottleHistory = BottleHistory(userId = targetUserId, matchedUserId = sourceUserId)
        bottleHistoryRepository.save(targetUserBottleHistory)
    }

    @Transactional
    fun saveRefuseHistory(sourceUserId: Long, targetUserId: Long) {
        val bottleHistory = BottleHistory(userId = sourceUserId, refusedUserId = targetUserId)
        bottleHistoryRepository.save(bottleHistory)
    }
}
