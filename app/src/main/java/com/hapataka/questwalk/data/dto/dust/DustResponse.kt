package com.hapataka.questwalk.data.dto.dust

import com.google.gson.annotations.SerializedName
import com.hapataka.questwalk.data.dto.dust.DustBody
import com.hapataka.questwalk.data.dto.dust.DustHeader

data class DustResponse(
    @SerializedName("body")
    val dustBody: com.hapataka.questwalk.data.dto.dust.DustBody,
    @SerializedName("header")
    val dustHeader: com.hapataka.questwalk.data.dto.dust.DustHeader
)