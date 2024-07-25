package com.nexters.bottles.bottle.service

import org.springframework.web.multipart.MultipartFile
import java.net.URL

interface FileService {

    fun upload(file: MultipartFile, path: String): URL
}
