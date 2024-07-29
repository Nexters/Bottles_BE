package com.nexters.bottles.user.domain

import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.user.facade.dto.InterestDto
import com.nexters.bottles.user.facade.dto.RegionDto
import com.nexters.bottles.user.repository.converter.QuestionAndAnswerConverter
import com.nexters.bottles.user.repository.converter.UserProfileSelectConverter
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Convert(converter = UserProfileSelectConverter::class)
    var profileSelect: UserProfileSelect? = null,

    @Convert(converter = QuestionAndAnswerConverter::class)
    var introduction: List<QuestionAndAnswer> = arrayListOf(),
) : BaseEntity()

data class UserProfileSelect(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: InterestDto,
    val job: String,
    val height: Int,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: RegionDto,
)

data class QuestionAndAnswer(
    val question: String,
    val answer: String,
)

