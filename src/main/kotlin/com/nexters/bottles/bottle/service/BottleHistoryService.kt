package com.nexters.bottles.bottle.service

import com.nexters.bottles.bottle.domain.BottleHistory
import com.nexters.bottles.bottle.repository.BottleHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BottleHistoryService(
    private val bottleHistoryRepository: BottleHistoryRepository
) {

    @Transactional
    fun saveBottleHistory(targetUserId: Long, matchedUserId: Long) {
        val bottleHistory = BottleHistory(userId = targetUserId, matchedUserId = matchedUserId)
        bottleHistoryRepository.save(bottleHistory)
    }
}
