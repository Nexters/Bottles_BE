package com.nexters.bottles.user.repository.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.nexters.bottles.user.domain.QuestionAndAnswer
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class QuestionAndAnswerConverter : AttributeConverter<List<QuestionAndAnswer>, String> {

    private val objectMapper = ObjectMapper().registerModule(kotlinModule())

    override fun convertToDatabaseColumn(attribute: List<QuestionAndAnswer>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): List<QuestionAndAnswer>? {
        return dbData?.let {
            objectMapper.readValue(
                it,
                objectMapper.typeFactory.constructCollectionType(List::class.java, QuestionAndAnswer::class.java)
            )
        }
    }
}
