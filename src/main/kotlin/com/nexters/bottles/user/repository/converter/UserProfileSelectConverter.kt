package com.nexters.bottles.user.repository.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nexters.bottles.user.domain.UserProfileSelect
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class UserProfileSelectConverter : AttributeConverter<UserProfileSelect, String> {

    private val objectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )

    override fun convertToDatabaseColumn(attribute: UserProfileSelect?): String {
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            throw RuntimeException("Error converting JSON to String", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): UserProfileSelect? {
        return try {
            dbData?.let { objectMapper.readValue(it, UserProfileSelect::class.java) }
        } catch (e: Exception) {
            throw RuntimeException("Error converting String to JSON", e)
        }
    }
}
