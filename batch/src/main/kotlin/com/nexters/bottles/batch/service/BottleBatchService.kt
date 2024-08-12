package com.nexters.bottles.batch.service

import com.nexters.bottles.app.bottle.repository.BottleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BottleBatchService(
    private val bottleRepository: BottleRepository
) {

    @Transactional
    fun deleteBottles() {
        val now = LocalDateTime.now()
        bottleRepository.updateAllDeletedTrueAndDeletedAtByStoppedAtBefore(now.minusDays(2), now)
    }
}