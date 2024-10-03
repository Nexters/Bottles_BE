package com.nexters.bottles.app.notification.component

import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.notification.component.dto.Block
import com.nexters.bottles.app.notification.component.dto.SlackMessage
import com.nexters.bottles.app.notification.component.dto.Text
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

    fun sendDailyStatistics() {
        log.info { "데일리 지표 스케줄러 돌기 시작" }
        log.info { "slackUrl=$slackUrl" }
        val yesterday = LocalDate.now().minusDays(1)

        val allUsers = userRepository.findAll()
        val currentUser = allUsers.filterNot { it.deleted }
        val yesterdayRegisterUser = allUsers.filter { it.createdAt.toLocalDate() == yesterday }
        val yesterdayLeaveUser = allUsers.filter { it.deletedAt?.toLocalDate() == yesterday }
        val yesterdayStartPingpong = bottleRepository
            .findAllByCreatedAtGreaterThanAndCreatedAtLessThan(
                LocalDateTime.of(yesterday, LocalTime.MIN),
                LocalDateTime.of(yesterday, LocalTime.MAX)
            )
            .filter { it.pingPongStatus == PingPongStatus.MATCHED }

        val request = mapOf(
            "channel" to slackChannel,
            "blocks" to listOf(
                mapOf(
                    "type" to "section",
                    "text" to mapOf(
                        "type" to "mrkdwn",
                        "text" to """
                    지표 물어다주는 새 :bird: 
                    전체 유저: ${currentUser} 명 
                    어제 가입 유저: ${yesterdayRegisterUser} 명 
                    어제 탈퇴 유저: ${yesterdayLeaveUser} 명 
                    어제 핑퐁 시작 유저: ${yesterdayStartPingpong} 명 
                """.trimIndent()
                    )
                )
            )
        )

        val response = webClient.post()
            .uri(slackUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        log.info { "response: $response" }
    }

    fun sendWeeklyStatistics() {
        log.info { "위클리 지표 스케줄러 돌기 시작" }
        val lastWeekMonday = LocalDate.now().minusDays(7)
        val lastWeekSunday = LocalDate.now().minusDays(1)

        val yesterdayUserProfile = userProfileRepository
            .findAllByCreatedAtGreaterThanAndCreatedAtLessThan(
                LocalDateTime.of(lastWeekMonday, LocalTime.MIN),
                LocalDateTime.of(lastWeekSunday, LocalTime.MAX)
            )

        val yesterdayIntroductionDone = yesterdayUserProfile.filter { it.introduction.isNotEmpty() }.count()
        val yesterdayIntroductionRatio = (yesterdayIntroductionDone / yesterdayUserProfile.count()) * 100
        val decimalFormat = DecimalFormat("#.##")
        val formattedRatio = decimalFormat.format(yesterdayIntroductionRatio)

        val request = mapOf(
            "channel" to slackChannel,
            "blocks" to listOf(
                mapOf(
                    "type" to "section",
                    "text" to mapOf(
                        "type" to "mrkdwn",
                        "text" to """
                            지표 물어다주는 새 :bird:\n\n
                            저번주 프로필   생성 수: ${yesterdayUserProfile.count()} \n\n
                            저번주 자기소개 작성 수: $yesterdayIntroductionDone \n\n
                            저번주 자기소개 작성 비율: ${formattedRatio}% \n\n
                """.trimIndent()
                    )
                )
            )
        )

        val response = webClient.post()
            .uri(slackUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }
}
