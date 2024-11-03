package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.BottleReadHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BottleReadHistoryRepository : JpaRepository<BottleReadHistory, Long> {
}
