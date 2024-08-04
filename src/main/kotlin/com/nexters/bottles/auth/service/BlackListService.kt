package com.nexters.bottles.auth.service

import com.nexters.bottles.auth.domain.BlackList
import com.nexters.bottles.auth.repository.BlackListRepository
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
}
