package com.nexters.bottles.app.auth.service

import com.nexters.bottles.app.auth.domain.BlackList
import com.nexters.bottles.app.auth.repository.BlackListRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlackListService(
    private val blackListRepository: BlackListRepository
) {

    @Transactional
    fun add(accessToken: String) {
        val blackList = BlackList(expiredAccessToken = accessToken)
        blackListRepository.save(blackList)
    }

    @Transactional(readOnly = true)
    fun findLastExpiredToken(token: String): BlackList? {
        val blackLists = blackListRepository.findAllByExpiredAccessToken(token)
        return if (blackLists.isEmpty()) {
            null
        } else {
            blackLists.last()
        }
    }
}
