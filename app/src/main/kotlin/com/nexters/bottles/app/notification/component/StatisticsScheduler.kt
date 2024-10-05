package com.nexters.bottles.app.notification.component

import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.common.annotation.LiveOnly
import com.nexters.bottles.app.user.domain.enum.Gender
import com.nexters.bottles.app.user.repository.UserProfileRepository
import com.nexters.bottles.app.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class StatisticsScheduler(
    private val userRepository: UserRepository,
    private val bottleRepository: BottleRepository,
    private val letterRepository: LetterRepository,
    private val userProfileRepository: UserProfileRepository,

    @Value("\${slack.webhook-url}")
    private val slackUrl: String,

    @Value("\${slack.channel.statistics}")
    private val slackChannel: String,
) {

    private val log = KotlinLogging.logger {}

    private val webClient = WebClient.builder()
        .baseUrl(slackUrl)
        .build()

    @LiveOnly
    @Scheduled(cron = "0 0 10 * * *")
    fun sendDailyStatistics() {
        val yesterday = LocalDate.now().minusDays(1)

        val allUsers = userRepository.findAll()
        val currentUser = allUsers.filterNot { it.deleted }.filter { it.id > 151 }
        val yesterdayRegisterUser = allUsers.filter { it.createdAt.toLocalDate() == yesterday }
        val yesterdayLeaveUser = allUsers.filter { it.deletedAt?.toLocalDate() == yesterday }
        val yesterdayStartPingpong = bottleRepository
            .findAllByCreatedAtGreaterThanAndCreatedAtLessThan(
                LocalDateTime.of(yesterday, LocalTime.MIN),
                LocalDateTime.of(yesterday, LocalTime.MAX)
            )
            .filter { it.pingPongStatus == PingPongStatus.MATCHED }

        val currentMaleUsers = currentUser.filter { it.gender == Gender.MALE }
        val currentFemaleUsers = currentUser.filter { it.gender == Gender.FEMALE }

        val currentIntroductionDoneMaleuser =
            userProfileRepository.findAllByUserIdIn(currentMaleUsers.map { it.id }).filter { it.introduction.isNotEmpty() }

        val currentIntroductionDoneFemaleuser =
            userProfileRepository.findAllByUserIdIn(currentFemaleUsers.map { it.id }).filter { it.introduction.isNotEmpty() }

        val request = mapOf(
            "channel" to slackChannel,
            "blocks" to listOf(
                mapOf(
                    "type" to "section",
                    "text" to mapOf(
                        "type" to "mrkdwn",
                        "text" to """
                    지표 물어다주는 새 :bird: 
                    전체 유저: ${currentUser.count()} 명 
                    자기소개 작성 남성 유저: ${currentIntroductionDoneMaleuser.count()} 명 
                    자기소개 작성 여성 유저: ${currentIntroductionDoneFemaleuser.count()} 명 
                    어제 가입 유저: ${yesterdayRegisterUser.count()} 명 
                    어제 탈퇴 유저: ${yesterdayLeaveUser.count()} 명 
                    어제 핑퐁 시작 유저: ${yesterdayStartPingpong.count()} 명 
                """.trimIndent()
                    )
                )
            )
        )

        val response = webClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    @LiveOnly
    @Scheduled(cron = "0 30 10 * * 1")
    fun sendWeeklyStatistics() {
        val lastWeekMonday = LocalDate.now().minusDays(7)
        val lastWeekSunday = LocalDate.now().minusDays(1)

        val lastWeekUserProfile = userProfileRepository
            .findAllByCreatedAtGreaterThanAndCreatedAtLessThan(
                LocalDateTime.of(lastWeekMonday, LocalTime.MIN),
                LocalDateTime.of(lastWeekSunday, LocalTime.MAX)
            )

        val lastWeekIntroductionDone = lastWeekUserProfile.filter { it.introduction.isNotEmpty() }
        val lastWeekIntroductionRatio = (lastWeekIntroductionDone.count() / lastWeekUserProfile.count()) * 100
        val decimalFormat = DecimalFormat("#.##")
        val formattedRatio = decimalFormat.format(lastWeekIntroductionRatio)

        val request = mapOf(
            "channel" to slackChannel,
            "blocks" to listOf(
                mapOf(
                    "type" to "section",
                    "text" to mapOf(
                        "type" to "mrkdwn",
                        "text" to """
                            지표 물어다주는 새 :bird:\n\n
                            저번주 프로필   생성 수: ${lastWeekUserProfile.count()}
                            저번주 자기소개 작성 수: ${lastWeekIntroductionDone.count()}
                            저번주 자기소개 작성 비율: ${formattedRatio}%
                """.trimIndent()
                    )
                )
            )
        )

        val response = webClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }
}
