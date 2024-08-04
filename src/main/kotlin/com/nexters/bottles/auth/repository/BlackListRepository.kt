package com.nexters.bottles.auth.repository

import com.nexters.bottles.auth.domain.BlackList
import org.springframework.data.jpa.repository.JpaRepository

interface BlackListRepository : JpaRepository<BlackList, Long> {

    fun findByExpiredAccessToken(token: String): BlackList?
}
