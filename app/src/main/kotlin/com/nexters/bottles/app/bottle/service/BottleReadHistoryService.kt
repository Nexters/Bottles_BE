package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.BottleReadHistory
import com.nexters.bottles.app.bottle.repository.BottleReadHistoryRepository
import com.nexters.bottles.app.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BottleReadHistoryService(
    private val bottleReadHistoryRepository: BottleReadHistoryRepository
) {

    @Transactional
    fun saveBottleReadHistory(user: User, bottle: Bottle) {
        val bottleReadHistory = BottleReadHistory(user = user, bottle = bottle)
        bottleReadHistoryRepository.save(bottleReadHistory)
    }

    @Transactional
    fun markReadUserBottle(bottle: Bottle, user: User) {
        val bottleReadHistory = bottleReadHistoryRepository.findByBottleAndUser(bottle, user)
        bottleReadHistory.markRead()
    }
}
