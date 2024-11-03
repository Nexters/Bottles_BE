package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.BottleReadHistory
import com.nexters.bottles.app.bottle.repository.BottleReadHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BottleReadHistoryService(
    private val bottleReadHistoryRepository: BottleReadHistoryRepository
) {

    @Transactional
    fun saveBottleReadHistory(userId: Long, bottleId: Long) {
        val bottleReadHistory = BottleReadHistory(userId = userId, bottleId = bottleId)
        bottleReadHistoryRepository.save(bottleReadHistory)
    }
}
