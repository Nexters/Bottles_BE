package com.nexters.bottles.api.user.controller

import com.nexters.bottles.api.global.interceptor.AuthRequired
import com.nexters.bottles.api.global.resolver.AuthUserId
import com.nexters.bottles.api.user.facade.UserProfileFacadeV2
import com.nexters.bottles.api.user.facade.dto.PresignedUrlsRequest
import com.nexters.bottles.api.user.facade.dto.PresignedUrlsResponse
import com.nexters.bottles.api.user.facade.dto.RegisterImageUrlsRequest
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/profile")
class UserProfileControllerV2(
    private val profileFacadeV2: UserProfileFacadeV2,
) {

    @ApiOperation("사진 여러장 업로드 S3 Presigned Url 발급받기")
    @GetMapping("/images/presigned-url")
    @AuthRequired
    fun getS3PresignedUrls(
        @AuthUserId userId: Long, @RequestBody presignedUrlsRequest: PresignedUrlsRequest
    ): PresignedUrlsResponse {
        return profileFacadeV2.getS3PresignedUrls(userId, presignedUrlsRequest)
    }

    @ApiOperation("사진 업로드 후 url 저장하기")
    @PostMapping("/images")
    @AuthRequired
    fun registerImageUrls(@AuthUserId userId: Long, @RequestBody registerImageUrlsRequest: RegisterImageUrlsRequest) {
        profileFacadeV2.registerImageUrls(userId, registerImageUrlsRequest)
    }
}
