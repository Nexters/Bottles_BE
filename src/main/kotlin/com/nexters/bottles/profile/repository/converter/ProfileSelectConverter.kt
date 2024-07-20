package com.nexters.bottles.profile.repository.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nexters.bottles.profile.domain.ProfileSelect
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class ProfileSelectConverter : AttributeConverter<ProfileSelect, String> {

    private val objectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )

    override fun convertToDatabaseColumn(attribute: ProfileSelect?): String {
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            throw RuntimeException("Error converting JSON to String", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): ProfileSelect? {
        return try {
            dbData?.let { objectMapper.readValue(it, ProfileSelect::class.java) }
        } catch (e: Exception) {
            throw RuntimeException("Error converting String to JSON", e)
        }
    }
}
