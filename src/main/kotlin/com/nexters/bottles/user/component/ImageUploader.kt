package com.nexters.bottles.user.component

import com.nexters.bottles.user.service.FileService
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.imageio.ImageIO

@Component
class ImageUploader(
    private val imageProcessor: ImageProcessor,
    private val fileService: FileService
) {

    fun upload(file: MultipartFile, path: String): URL {
        return fileService.upload(file, path)
    }

    fun uploadWithBlur(file: MultipartFile, path: String): URL {
        val uploadDir = Paths.get("uploads")
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        val originalFilePath = uploadDir.resolve("original_" + LocalDateTime.now() + file.originalFilename)
        Files.copy(file.inputStream, originalFilePath)

        val blurredImage = imageProcessor.blurImage(File(originalFilePath.toString()))

        val blurredFilePath = uploadDir.resolve("blurred_" + LocalDateTime.now() + file.originalFilename)
        ImageIO.write(blurredImage, "jpg", File(blurredFilePath.toString()))

        val imageUrlBlur = fileService.upload(blurredFilePath.toString(), "blurred_${path}")

        Files.deleteIfExists(originalFilePath)
        Files.deleteIfExists(blurredFilePath)

        return imageUrlBlur
    }
}
