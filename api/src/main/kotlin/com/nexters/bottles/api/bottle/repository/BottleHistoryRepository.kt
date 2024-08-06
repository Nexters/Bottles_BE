package com.nexters.bottles.api.bottle.repository

import com.nexters.bottles.api.bottle.domain.BottleHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BottleHistoryRepository : JpaRepository<BottleHistory, Long> {
}
