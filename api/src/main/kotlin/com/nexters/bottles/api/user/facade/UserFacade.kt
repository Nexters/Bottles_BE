package com.nexters.bottles.api.user.facade

import com.nexters.bottles.api.user.facade.dto.ReportUserRequest
import com.nexters.bottles.app.user.domain.UserReport
import com.nexters.bottles.app.user.service.UserReportService
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userReportService: UserReportService,
) {

    fun reportUser(userId: Long, reportUserRequest: ReportUserRequest) {
        userReportService.saveReport(
            UserReport(
                reporterUserId = userId,
                respondentUserId = reportUserRequest.userId,
                reportReasonShortAnswer = reportUserRequest.reportReasonShortAnswer,
            )
        )
    }
}
