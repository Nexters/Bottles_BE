package com.nexters.bottles.user.domain

import com.nexters.bottles.user.facade.dto.InterestDto
import com.nexters.bottles.user.facade.dto.RegionDto
import com.nexters.bottles.user.repository.converter.QuestionAndAnswerConverter
import com.nexters.bottles.user.repository.converter.UserProfileSelectConverter
import javax.persistence.*

@Entity
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Convert(converter = UserProfileSelectConverter::class)
    var profileSelect: UserProfileSelect? = null,

    @Convert(converter = QuestionAndAnswerConverter::class)
    var introduction: List<QuestionAndAnswer> = arrayListOf(),
) :  BaseEntity()

data class UserProfileSelect(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: InterestDto,
    val job: String,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: RegionDto,
)

data class QuestionAndAnswer(
    val question: String,
    val answer: String,
)

