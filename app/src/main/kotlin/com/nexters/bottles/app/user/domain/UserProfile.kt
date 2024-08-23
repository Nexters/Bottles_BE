package com.nexters.bottles.app.user.domain

import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.repository.converter.QuestionAndAnswerConverter
import com.nexters.bottles.app.user.repository.converter.UserProfileSelectConverter
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Convert(converter = UserProfileSelectConverter::class)
    var profileSelect: UserProfileSelect? = null,

    @Convert(converter = QuestionAndAnswerConverter::class)
    var introduction: List<QuestionAndAnswer> = arrayListOf(),

    var imageUrl: String? = null,
) : BaseEntity() {

    fun hasCompleteIntroduction(): Boolean {
        return introduction.isNotEmpty()
    }

    fun isNotRegisterIntroductionOrImage(): Boolean {
        return introduction.isEmpty() || imageUrl == null
    }
}

data class UserProfileSelect(
    val mbti: String,
    val keyword: List<String> = arrayListOf(),
    val interest: Interest,
    val job: String,
    val height: Int,
    val smoking: String,
    val alcohol: String,
    val religion: String,
    val region: Region,
)

data class Interest(
    val culture: List<String> = arrayListOf(),
    val sports: List<String> = arrayListOf(),
    val entertainment: List<String> = arrayListOf(),
    val etc: List<String> = arrayListOf(),
)

data class Region(
    val city: String,
    val state: String
)

data class QuestionAndAnswer(
    val question: String,
    val answer: String,
)
