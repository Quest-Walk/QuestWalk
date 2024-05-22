package com.hapataka.questwalk.data.dto.dust

import com.google.gson.annotations.SerializedName

data class Dust(
    @SerializedName("response")
    val dustResponse: com.hapataka.questwalk.data.dto.dust.DustResponse
)