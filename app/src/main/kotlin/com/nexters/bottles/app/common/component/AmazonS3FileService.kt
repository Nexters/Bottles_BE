package com.nexters.bottles.app.common.component

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

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

    fun downloadAsMultipartFile(key: String): MultipartFile {
        // S3에서 파일 가져오기
        println("key: $key")
        val s3Object = amazonS3.getObject(GetObjectRequest(bucket, key))
        val inputStream = s3Object.objectContent

        // 바이트 배열로 변환
        val outputStream = ByteArrayOutputStream()
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        // S3 파일 이름 및 MIME 타입 설정
        val fileName = key.substringAfterLast("/")
        val contentType = Files.probeContentType(Paths.get(fileName)) ?: "application/octet-stream"

        // CustomMultipartFile로 변환
        return CustomMultipartFile(
            outputStream.toByteArray(),
            fileName,
            contentType
        )
    }
}

class CustomMultipartFile(
    private val content: ByteArray,
    private val fileName: String,
    private val contentType: String
) : MultipartFile {

    override fun getName(): String = fileName
    override fun getOriginalFilename(): String = fileName
    override fun getContentType(): String = contentType
    override fun isEmpty(): Boolean = content.isEmpty()
    override fun getSize(): Long = content.size.toLong()
    override fun getBytes(): ByteArray = content
    override fun getInputStream(): InputStream = ByteArrayInputStream(content)
    override fun transferTo(dest: File) {
        FileOutputStream(dest).use { it.write(content) }
    }
}
