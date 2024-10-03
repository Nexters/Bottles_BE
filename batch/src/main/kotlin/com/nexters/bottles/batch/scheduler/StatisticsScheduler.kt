package com.nexters.bottles.batch.scheduler

import com.nexters.bottles.app.bottle.domain.enum.PingPongStatus
import com.nexters.bottles.app.bottle.repository.BottleRepository
import com.nexters.bottles.app.bottle.repository.LetterRepository
import com.nexters.bottles.app.user.repository.UserProfileRepository
import com.nexters.bottles.app.user.repository.UserRepository
import com.nexters.bottles.batch.scheduler.dto.Block
import com.nexters.bottles.batch.scheduler.dto.SlackMessage
import com.nexters.bottles.batch.scheduler.dto.Text
import org.springframework.beans.factory.annotation.Value
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

    val webClient = WebClient.builder()
        .baseUrl(slackUrl)
        .build()

    @Scheduled(cron = "0 0 13 * * *")
    fun sendStatistics() {
        val yesterday = LocalDate.now().minusDays(1)

        val allUsers = userRepository.findAll()
        val currentUser = allUsers.filterNot { it.deleted }
        val yesterdayRegisterUser = allUsers.filter { it.createdAt.toLocalDate() == yesterday }
        val yesterdayLeaveUser = allUsers.filter { it.deletedAt?.toLocalDate() == yesterday }
        val yesterdayStartPingpong = bottleRepository
            .findAllByCreatedAtGreaterThanAndCreatedAtLessThan(LocalDateTime.of(yesterday, LocalTime.MIN), LocalDateTime.of(yesterday, LocalTime.MAX))
            .filter { it.pingPongStatus == PingPongStatus.MATCHED }

        val yesterdayUserProfile = userProfileRepository
            .findAllByCreatedAtGreaterThanAndCreatedAtLessThan(LocalDateTime.of(yesterday, LocalTime.MIN), LocalDateTime.of(yesterday, LocalTime.MAX))


        val yesterdayIntroductionDone = yesterdayUserProfile.filter { it.introduction.isNotEmpty() }.count()
        val yesterdayIntroductionRatio = (yesterdayIntroductionDone / yesterdayUserProfile.count()) * 100
        val decimalFormat = DecimalFormat("#.##")
        val formattedRatio = decimalFormat.format(yesterdayIntroductionRatio)

        val request = SlackMessage(
            channel = slackChannel,
            blocks = listOf(
                Block(
                    type = "section",
                    text = Text(
                        type = "mrkdwn",
                        text = """
                            지표 물어다주는 새 :bird:*\n\n
                            전체 유저: ${currentUser} 명 *\n\n
                            어제 가입 유저: ${yesterdayRegisterUser} 명 *\n\n
                            어제 탈퇴 유저: ${yesterdayLeaveUser} 명 *\n\n
                            어제 핑퐁 시작 유저: ${yesterdayStartPingpong} 명 *\n\n
                            어제 자기소개 작성 비율: ${formattedRatio}% *\n\n
                        """.trimIndent()
                    )
                )
            )
        )

        val response = webClient.post()
            .uri(slackUrl)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
    }
}
