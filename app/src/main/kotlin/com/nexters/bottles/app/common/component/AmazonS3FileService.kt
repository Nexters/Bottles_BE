package com.nexters.bottles.app.common.component

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.URL
import java.nio.file.Files

@Component
class AmazonS3FileService(
    private val amazonS3: AmazonS3,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
) : FileService {

    override fun upload(file: MultipartFile, key: String): URL {
        val objectMetadata = ObjectMetadata().apply {
            this.contentType = file.contentType
            this.contentLength = file.size
        }

        val putObjectRequest = PutObjectRequest(
            bucket,
            key,
            file.inputStream,
            objectMetadata
        )
        amazonS3.putObject(putObjectRequest)
        return amazonS3.getUrl(bucket, key)
    }

    override fun upload(filePath: String, key: String): URL {
        val file = File(filePath)
        val objectMetadata = ObjectMetadata().apply {
            this.contentType = Files.probeContentType(file.toPath())
            this.contentLength = file.length()
        }

        val putObjectRequest = PutObjectRequest(
            bucket,
            key,
            file.inputStream().buffered(),
            objectMetadata
        )
        amazonS3.putObject(putObjectRequest)
        return amazonS3.getUrl(bucket, key)
    }
}
