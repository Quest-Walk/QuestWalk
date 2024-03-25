package com.hapataka.questwalk.data.remote.dto.dust

import com.google.gson.annotations.SerializedName
import com.hapataka.questwalk.data.remote.dto.dust.DustBody
import com.hapataka.questwalk.data.remote.dto.dust.DustHeader

data class DustResponse(
    @SerializedName("body")
    val dustBody: DustBody,
    @SerializedName("header")
    val dustHeader: DustHeader
)