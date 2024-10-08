package com.nexters.bottles.app.bottle.domain

import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.LetterLastStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.domain.vo.LikeMessage
import com.nexters.bottles.app.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LetterTest {

    companion object {
        private val TARGET_USER = User(name = "user1")
        private val SOURCE_USER = User(name = "user2")
        private val PING_PONG_BOTTLE = Bottle(
            targetUser = TARGET_USER, sourceUser = SOURCE_USER, likeMessage = LikeMessage("hi"),
            bottleStatus = BottleStatus.SENT, pingPongStatus = PingPongStatus.ACTIVE
        );
        private val MY_LETTERS = listOf(
            LetterQuestionAndAnswer(question = "question1"),
            LetterQuestionAndAnswer(question = "question2"),
            LetterQuestionAndAnswer(question = "question3")
        )
        private val OTHER_LETTERS = listOf(
            LetterQuestionAndAnswer(question = "question1"),
            LetterQuestionAndAnswer(question = "question2"),
            LetterQuestionAndAnswer(question = "question3")
        )
    }

    @Nested
    inner class `문답의 가장 마지막 상태를 반환한다` {

        @Test
        fun `대화는 시작했으나 두 사람 모두 문답을 작성하지 않았을 경우`() {
            val myLetter = Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS)
            val otherLetter = Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.NO_ANSWER_FROM_BOTH)
        }

        @Test
        fun `내가 문답을 작성했을 경우`() {
            val myLetter = Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS)
            val otherLetter = Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS)
            myLetter.registerAnswer(1, "답변")

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.ANSWER_FROM_ME_ONLY)
        }

        @Test
        fun `상대방이 문답을 작성했을 경우`() {
            val myLetter = Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS)
            val otherLetter = Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS)
            otherLetter.registerAnswer(1, "답변")

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.ANSWER_FROM_OTHER)
        }

        @Test
        fun `내가 사진을 공유한 경우`() {
            val myLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS, isShareImage = true)
            val otherLetter = Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.PHOTO_SHARED_BY_ME_ONLY)
        }

        @Test
        fun `상대방이 사진을 공유한 경우`() {
            val myLetter = Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS)
            val otherLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS, isShareImage = true)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.PHOTO_SHARED_BY_OTHER)
        }

        @Test
        fun `둘 다 사진을 공유한 경우`() {
            val myLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS, isShareImage = true)
            val otherLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS, isShareImage = true)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.PHOTO_SHARED_BY_OTHER)
        }

        @Test
        fun `내가 연락처를 공유한 경우`() {
            val myLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS, isShareContact = true)
            val otherLetter = Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.CONTACT_SHARED_BY_ME_ONLY)
        }

        @Test
        fun `상대방이 연락처를 공유한 경우`() {
            val myLetter = Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS)
            val otherLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS, isShareContact = true)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.CONTACT_SHARED_BY_OTHER)
        }

        @Test
        fun `둘 다 연락처를 공유한 경우`() {
            val myLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = TARGET_USER, letters = MY_LETTERS, isShareContact = true)
            val otherLetter =
                Letter(bottle = PING_PONG_BOTTLE, user = SOURCE_USER, letters = OTHER_LETTERS, isShareContact = true)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.CONTACT_SHARED_BY_OTHER)
        }

        @Test
        fun `대화가 중단된 경우`() {
            val stoppedBottle = Bottle(
                targetUser = TARGET_USER, sourceUser = SOURCE_USER, likeMessage = LikeMessage("hi"),
                bottleStatus = BottleStatus.SENT, pingPongStatus = PingPongStatus.STOPPED
            );
            val myLetter =
                Letter(bottle = stoppedBottle, user = TARGET_USER, letters = MY_LETTERS, isShareContact = true)
            val otherLetter =
                Letter(bottle = stoppedBottle, user = SOURCE_USER, letters = OTHER_LETTERS, isShareContact = true)

            val lastStatus = myLetter.findLastStatusWithOtherLetter(otherLetter)

            assertThat(lastStatus).isEqualTo(LetterLastStatus.CONVERSATION_STOPPED)
        }
    }
}
