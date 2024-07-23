package com.nexters.bottles.pingpong.repository.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.nexters.bottles.config.JacksonConfig.Companion.kotlinModule
import com.nexters.bottles.pingpong.domain.LetterQuestionAndAnswer
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class LetterQuestionAndAnswerConverter : AttributeConverter<List<LetterQuestionAndAnswer>, String> {

    private val objectMapper = ObjectMapper().registerModule(kotlinModule)

    override fun convertToDatabaseColumn(attribute: List<LetterQuestionAndAnswer>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): List<LetterQuestionAndAnswer>? {
        return dbData?.let {
            objectMapper.readValue(
                it,
                objectMapper.typeFactory.constructCollectionType(List::class.java, LetterQuestionAndAnswer::class.java)
            )
        }
    }
}
