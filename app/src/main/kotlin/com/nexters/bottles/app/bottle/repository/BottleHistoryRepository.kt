package com.nexters.bottles.app.bottle.repository

import com.nexters.bottles.app.bottle.domain.BottleHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BottleHistoryRepository : JpaRepository<BottleHistory, Long> {
}
