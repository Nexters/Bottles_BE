package com.nexters.bottles.app.user.repository

import com.nexters.bottles.app.user.domain.UserReport
import org.springframework.data.jpa.repository.JpaRepository

interface UserReportRepository : JpaRepository<UserReport, Long> {

    fun findByReporterUserId(userId: Long): List<UserReport>
}
