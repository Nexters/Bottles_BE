package com.nexters.bottles.bottle.repository

import com.nexters.bottles.bottle.domain.BottleHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BottleHistoryRepository : JpaRepository<BottleHistory, Long> {
}
