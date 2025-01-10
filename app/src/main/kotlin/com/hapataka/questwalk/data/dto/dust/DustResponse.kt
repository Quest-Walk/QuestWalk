package com.hapataka.questwalk.data.dto.dust

import com.google.gson.annotations.SerializedName

data class DustResponse(
    @SerializedName("body")
    val dustBody: DustBody,
    @SerializedName("header")
    val dustHeader: DustHeader,
)