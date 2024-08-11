package com.nexters.bottles.app.user.service

import com.nexters.bottles.app.user.domain.UserReport
import com.nexters.bottles.app.user.repository.UserReportRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserReportService(
    private val userReportRepository: UserReportRepository,
) {

    @Transactional
    fun saveReport(userReport: UserReport) {
        userReportRepository.save(userReport)
    }

    @Transactional
    fun getReportRespondentList(userId: Long): List<UserReport> {
        return userReportRepository.findByReporterUserId(userId)
    }
}
