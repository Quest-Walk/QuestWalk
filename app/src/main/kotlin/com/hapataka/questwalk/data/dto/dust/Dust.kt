package com.hapataka.questwalk.data.dto.dust

import com.google.gson.annotations.SerializedName

data class Dust(
    @SerializedName("response")
    val dustResponse: DustResponse,
)