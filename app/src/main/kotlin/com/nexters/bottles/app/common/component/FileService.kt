package com.nexters.bottles.app.common.component

import org.springframework.web.multipart.MultipartFile
import java.net.URL

interface FileService {

    fun upload(file: MultipartFile, path: String): URL
    fun upload(filePath: String, key: String): URL
}
