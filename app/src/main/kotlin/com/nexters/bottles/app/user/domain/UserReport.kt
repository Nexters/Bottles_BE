package com.nexters.bottles.app.user.domain

import com.nexters.bottles.app.common.BaseEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class UserReport(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    private val reporterUserId: Long,   // 신고 한 유저

    val respondentUserId: Long, // 신고 대상 유저

    private val reportReasonShortAnswer: String? = null,
) : BaseEntity() {
}
