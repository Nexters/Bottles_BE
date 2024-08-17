package com.nexters.bottles.app.bottle.domain

import com.nexters.bottles.app.bottle.repository.converter.LetterQuestionAndAnswerConverter
import com.nexters.bottles.app.common.BaseEntity
import com.nexters.bottles.app.user.domain.User
import java.time.LocalDateTime
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
    var isShareImage: Boolean? = null,

    @Column
    var isShareContact: Boolean? = null,

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

    fun shareImage(willShare: Boolean) {
        isShareImage = willShare
    }

    fun shareContact(willShare: Boolean) {
        isShareContact = willShare
    }

    fun stopPingPong(stoppedBy: User, stoppedAt: LocalDateTime) {
        bottle.stop(stoppedBy, stoppedAt)
    }

    fun finishIfAllShare() {
        if (bottle.hasFirstSelectUser()) {
            bottle.match()
        } else {
            bottle.markFirstSelectUser(user)
        }
    }

    fun notFinishedLastAnswer(): Boolean {
        return letters.last().answer == null
    }
}

data class LetterQuestionAndAnswer(
    val question: String,
    var answer: String? = null,
)
