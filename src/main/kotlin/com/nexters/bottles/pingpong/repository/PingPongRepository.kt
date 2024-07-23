package com.nexters.bottles.pingpong.repository

import com.nexters.bottles.pingpong.domain.PingPong
import org.springframework.data.jpa.repository.JpaRepository

interface PingPongRepository : JpaRepository<PingPong, Long> {
}
