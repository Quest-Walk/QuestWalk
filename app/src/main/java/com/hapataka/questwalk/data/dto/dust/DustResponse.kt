package com.hapataka.questwalk.data.dto.dust

import com.google.gson.annotations.SerializedName

data class DustResponse(
    @SerializedName("body")
    val dustBody: com.hapataka.questwalk.data.dto.dust.DustBody,
    @SerializedName("header")
    val dustHeader: com.hapataka.questwalk.data.dto.dust.DustHeader
)