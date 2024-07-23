package com.nexters.bottles.pingpong.domain

import com.nexters.bottles.global.BaseEntity
import com.nexters.bottles.pingpong.repository.converter.LetterQuestionAndAnswerConverter
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
    @JoinColumn(name = "ping_pong_id")
    val pingPong: PingPong,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Convert(converter = LetterQuestionAndAnswerConverter::class)
    var letters: List<LetterQuestionAndAnswer> = arrayListOf(),

    @Column
    var image: String? = null,

    @Column
    var isRead: Boolean = false,
) : BaseEntity()

data class LetterQuestionAndAnswer(
    val question: String,
    val answer: String? = null,
)
