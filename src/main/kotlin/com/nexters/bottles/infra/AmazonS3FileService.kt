package com.nexters.bottles.infra

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.nexters.bottles.bottle.service.FileService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class AmazonS3FileService(
    private val amazonS3: AmazonS3,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
) : FileService {

    override fun upload(file: MultipartFile): URL {
        val objectMetadata = ObjectMetadata().apply {
            this.contentType = file.contentType
            this.contentLength = file.size
        }
        val key = makeKey(file)
        val putObjectRequest = PutObjectRequest(
            bucket,
            key,
            file.inputStream,
            objectMetadata,
        )
        amazonS3.putObject(putObjectRequest)
        return amazonS3.getUrl(bucket, key)
    }

    private fun makeKey(file: MultipartFile) =
        file.originalFilename + FILE_NAME_DELIMITER +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

    companion object {
        private const val FILE_NAME_DELIMITER = "_"
    }
}
