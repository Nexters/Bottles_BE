package com.nexters.bottles.app.auth.repository

import com.nexters.bottles.app.auth.domain.BlackList
import org.springframework.data.jpa.repository.JpaRepository

interface BlackListRepository : JpaRepository<BlackList, Long> {

    fun findByExpiredAccessToken(token: String): BlackList?
}
