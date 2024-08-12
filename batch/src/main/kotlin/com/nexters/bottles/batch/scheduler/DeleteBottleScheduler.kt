package com.nexters.bottles.batch.scheduler

import com.nexters.bottles.batch.service.BottleBatchService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DeleteBottleScheduler(
    private val bottleBatchService: BottleBatchService
) {

    @Scheduled(cron = "0 0 0 * * *")
    fun deleteBottles() {
        bottleBatchService.deleteBottles()
    }
}