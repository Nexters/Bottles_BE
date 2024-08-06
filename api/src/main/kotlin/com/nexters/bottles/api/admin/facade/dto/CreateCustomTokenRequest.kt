package com.nexters.bottles.api.admin.facade.dto

import io.swagger.annotations.ApiModelProperty

data class CreateCustomTokenRequest(

    @ApiModelProperty(value = "초 단위로 입력해주세요.", example = "30")
    val accessTime: Long,

    @ApiModelProperty(value = "초 단위로 입력해주세요.", example = "60")
    val refreshTime: Long
)
