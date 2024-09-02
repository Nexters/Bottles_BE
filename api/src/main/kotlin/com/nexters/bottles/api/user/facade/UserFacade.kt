package com.nexters.bottles.api.user.facade

import com.nexters.bottles.api.user.facade.dto.ReportUserRequest
import com.nexters.bottles.app.user.domain.BlockContact
import com.nexters.bottles.app.user.domain.UserReport
import com.nexters.bottles.app.user.service.BlockContactListService
import com.nexters.bottles.app.user.service.UserReportService
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userReportService: UserReportService,
    private val blockContactListService: BlockContactListService,
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


    fun blockContactList(userId: Long, blockContacts: Set<String>) {
        val savedBlockContacts = blockContactListService.findAllByUserId(userId = userId).map { it.phoneNumber }.toSet()
        val newBlockContacts = blockContacts.minus(savedBlockContacts).map { BlockContact(userId = userId, phoneNumber = it) }.toList()
        val deletedBlockContacts = savedBlockContacts.minus(blockContacts).map { BlockContact(userId = userId, phoneNumber = it) }.toList()

        blockContactListService.saveAll(newBlockContacts)
        blockContactListService.deleteAll(deletedBlockContacts)
    }
}
