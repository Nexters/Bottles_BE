package com.nexters.bottles.app.user.repository.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.nexters.bottles.app.config.JacksonConfig.Companion.kotlinModule
import com.nexters.bottles.app.user.domain.QuestionAndAnswer
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class ImagesConverter : AttributeConverter<List<String>, String> {

    private val objectMapper = ObjectMapper().registerModule(kotlinModule)

    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): List<String>? {
        return dbData?.let {
            objectMapper.readValue(
                it,
                objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
            )
        }
    }
}
