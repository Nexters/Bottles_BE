package com.nexters.bottles.bottle.domain

import com.nexters.bottles.bottle.repository.converter.LetterQuestionAndAnswerConverter
import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.user.domain.User
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity
class Letter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bottle_id")
    val bottle: Bottle,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Convert(converter = LetterQuestionAndAnswerConverter::class)
    var letters: List<LetterQuestionAndAnswer> = arrayListOf(),

    @Column
    var isShowImage: Boolean? = null,

    @Column
    var isReadByOtherUser: Boolean = false,
) : BaseEntity() {

    fun registerAnswer(order: Int, answer: String) {
        require(order >= 0 && order <= letters.size) {
            "고객센터에 문의해주세요"
        }
        letters[order - 1].answer = answer
    }

    fun markRead() {
        isReadByOtherUser = true
    }

    fun markUnread() {
        isReadByOtherUser = false
    }
}

data class LetterQuestionAndAnswer(
    val question: String,
    var answer: String? = null,
)
