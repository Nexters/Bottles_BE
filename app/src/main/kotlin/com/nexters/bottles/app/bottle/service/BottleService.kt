package com.nexters.bottles.app.bottle.service

import com.nexters.bottles.app.bottle.domain.Bottle
import com.nexters.bottles.app.bottle.domain.Letter
import com.nexters.bottles.app.bottle.domain.LetterQuestionAndAnswer
import com.nexters.bottles.app.bottle.domain.Question
import com.nexters.bottles.app.bottle.domain.enum.BottleStatus
import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.repository.BottleMatchingRepository
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.bottle.repository.dto.UsersCanBeMatchedDto
import com.nexters.bottles.app.user.domain.User
import com.nexters.bottles.app.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class BottleService(
    private val bottleRepository: BottleRepository,
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val bottleMatchingRepository: BottleMatchingRepository,
) {

    private val log = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun getNewBottles(user: User): List<Bottle> {
        return bottleRepository.findAllByTargetUserAndStatusAndNotExpiredAndDeletedFalse(
            user,
            PingPongStatus.NONE,
            LocalDateTime.now()
        )
    }

    @Transactional(readOnly = true)
    fun getNotExpiredBottle(
        bottleId: Long,
        statusSet: Set<PingPongStatus>
    ): Bottle {
        return bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(bottleId, statusSet, LocalDateTime.now())
            ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")
    }

    @Transactional
    fun acceptBottle(userId: Long, bottleId: Long, likeMessage: String?, questions: List<Question>): Bottle {
        val bottle =
            bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(
                bottleId,
                setOf(PingPongStatus.NONE),
                LocalDateTime.now()
            ) ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        when (bottle.bottleStatus) {
            BottleStatus.RANDOM -> {
                requireNotNull(likeMessage) { "고객센터에 문의해주세요" }
                bottle.sendLikeMessage(
                    from = bottle.targetUser,
                    to = bottle.sourceUser,
                    likeMessage = likeMessage,
                    LocalDateTime.now()
                )
            }

            BottleStatus.SENT -> {
                require(likeMessage == null) { "고객센터에 문의해주세요" }
                bottle.startPingPong()

                val letters = findRandomQuestions(questions)
                saveLetter(bottle, bottle.targetUser, letters)
                saveLetter(bottle, bottle.sourceUser, letters)
            }
        }
        return bottle
    }

    private fun findRandomQuestions(questions: List<Question>) = questions
        .shuffled()
        .take(3)
        .map {
            LetterQuestionAndAnswer(question = it.question)
        }

    private fun saveLetter(
        bottle: Bottle,
        user: User,
        letters: List<LetterQuestionAndAnswer>
    ) {
        val letter = Letter(bottle = bottle, user = user, letters = letters)
        letterRepository.save(letter)
    }

    @Transactional
    fun refuseBottle(userId: Long, bottleId: Long): Bottle {
        val bottle =
            bottleRepository.findByIdAndStatusAndNotExpiredAndDeletedFalse(
                bottleId,
                setOf(PingPongStatus.NONE),
                LocalDateTime.now()
            ) ?: throw IllegalArgumentException("이미 떠내려간 보틀이에요")

        val refusedUser =
            userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        bottle.refuse(refusedUser)
        return bottle
    }

    @Transactional
    fun stop(userId: Long, bottleId: Long): Bottle {
        val bottle = bottleRepository.findByIdAndStatusAndDeletedFalse(
            bottleId,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        ) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
        val stoppedUser =
            userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")

        bottle.stop(stoppedUser, LocalDateTime.now())
        return bottle
    }

    @Transactional(readOnly = true)
    fun getPingPongBottles(userId: Long): List<Bottle> {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw IllegalStateException("회원가입 상태를 문의해주세요")
        return bottleRepository.findAllByUserAndStatusAndDeletedFalse(
            user,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        )
    }

    @Transactional(readOnly = true)
    fun getPingPongBottle(bottleId: Long): Bottle {
        return bottleRepository.findByIdAndStatusAndDeletedFalse(
            bottleId,
            setOf(
                PingPongStatus.ACTIVE,
                PingPongStatus.MATCHED,
                PingPongStatus.STOPPED
            )
        ) ?: throw IllegalArgumentException("고객센터에 문의해주세요")
    }

    @Transactional
    fun matchRandomBottle(user: User, matchingHour: Int): Bottle? {
        if (user.isNotRegisterProfile()) return null
        if (user.isMatchInactive()) return null

        val matchingTime = getMatchingTime(matchingHour)
        val todayMatchingBottle = bottleRepository.findByTargetUserAndBottleStatusAndCreatedAtAfter(
            targetUser = user,
            bottleStatus = BottleStatus.RANDOM,
            matchingTime = matchingTime
        )
        if (todayMatchingBottle.isNotEmpty()) return null

        log.info { "userId: ${user.id}, gender: ${user.gender}" }
        val usersCanBeMatched = bottleMatchingRepository.findAllUserCanBeMatched(user.id, user.gender)
        if (usersCanBeMatched.isEmpty()) return null

        val matchingUserDto = findUserSameRegionOrRandom(usersCanBeMatched, user)
        val matchingUser = userRepository.findByIdAndDeletedFalse(matchingUserDto.willMatchUserId)
            ?: throw IllegalArgumentException("탈퇴한 회원입니다")

        val bottle = Bottle(targetUser = user, sourceUser = matchingUser, expiredAt = matchingTime.plusDays(1))
        return bottleRepository.save(bottle)
    }

    private fun getMatchingTime(matchingHour: Int): LocalDateTime {
        val now = LocalDateTime.now()
        var matchingTime = now.with(LocalTime.of(matchingHour, 0))
        if (now.hour < matchingHour) {
            matchingTime = matchingTime.minusDays(1)
        }
        return matchingTime
    }

    private fun findUserSameRegionOrRandom(
        usersCanBeMatchedDtos: List<UsersCanBeMatchedDto>,
        targetUser: User
    ): UsersCanBeMatchedDto {
        return usersCanBeMatchedDtos.shuffled()
            .firstOrNull {
                targetUser.gender.name != it.willMatchUserGender
                targetUser.city == it.willMatchCity
            } ?: usersCanBeMatchedDtos[0]
    }
}
