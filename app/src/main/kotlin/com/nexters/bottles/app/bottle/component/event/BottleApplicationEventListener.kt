package com.nexters.bottles.app.bottle.component.event

import com.nexters.bottles.app.bottle.component.event.dto.AcceptBottleEventDto
import com.nexters.bottles.app.bottle.service.LetterService
import com.nexters.bottles.app.bottle.service.QuestionService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class BottleApplicationEventListener(
    private val questionService: QuestionService,
    private val letterService: LetterService,
) {

    @TransactionalEventListener
    fun handleCustomEvent(event: AcceptBottleEventDto) {
        val allQuestions = questionService.findAllQuestions()
        val questions = allQuestions.shuffled()
            .take(3)

        letterService.saveLetter(event.bottleId, questions)
    }
}
