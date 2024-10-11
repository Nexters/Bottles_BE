package com.nexters.bottles.app.common.component

import org.springframework.http.HttpMethod
import org.springframework.web.multipart.MultipartFile
import java.net.URL

interface FileService {

    fun upload(file: MultipartFile, key: String): URL
    fun upload(filePath: String, key: String): URL
    fun getPresignedUrl(filePath: String, httpMethod: HttpMethod): URL
    fun remove(fileUrl: String)
}
