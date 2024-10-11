package com.nexters.bottles.api.user.facade

import com.nexters.bottles.api.user.facade.dto.PresignedUrlsRequest
import com.nexters.bottles.api.user.facade.dto.PresignedUrlsResponse
import com.nexters.bottles.api.user.facade.dto.RegisterImageUrlsRequest
import com.nexters.bottles.app.common.component.FileService
import com.nexters.bottles.app.user.service.UserProfileService
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class UserProfileFacadeV2(
    private val profileService: UserProfileService,
    private val fileService: FileService,
) {

    fun getS3PresignedUrls(userId: Long, presignedUrlsRequest: PresignedUrlsRequest): PresignedUrlsResponse {
        return PresignedUrlsResponse(
            presignedUrlsRequest.fileNames.mapIndexed { index, fileName ->
                val filePath = when (index) {
                    0 -> makeFirstPathWithUserId(fileName, userId)
                    else -> makePathWithUserId(fileName, userId)
                }
                fileService.getPresignedUrl(filePath, HttpMethod.PUT).toString()
            }.toList()
        )
    }

    fun registerImageUrls(userId: Long, registerImageUrlsRequest: RegisterImageUrlsRequest) {
        profileService.upsertImageUrls(
            userId,
            registerImageUrlsRequest.imageUrls,
            registerImageUrlsRequest.imageUrls[0].replace(PREFIX_ORIGINAL_IMAGE_MAIN, PREFIX_BLURRED_IMAGE)
        )
    }

    private fun makeFirstPathWithUserId(
        fileName: String,
        userId: Long
    ): String {
        val filePath = "${PREFIX_ORIGINAL_IMAGE_MAIN}${userId}${FILE_NAME_DELIMITER}${
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        }${FILE_NAME_DELIMITER}${fileName}"

        return filePath
    }

    private fun makePathWithUserId(
        fileName: String,
        userId: Long
    ): String {
        val filePath = "${PREFIX_ORIGINAL_IMAGE}${userId}${FILE_NAME_DELIMITER}${
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        }${FILE_NAME_DELIMITER}${fileName}"

        return filePath
    }

    companion object {
        private const val FILE_NAME_DELIMITER = "_"
        private const val PREFIX_ORIGINAL_IMAGE_MAIN = "original/main/"
        private const val PREFIX_ORIGINAL_IMAGE = "original/"
        private const val PREFIX_BLURRED_IMAGE = "blur/"
    }
}
