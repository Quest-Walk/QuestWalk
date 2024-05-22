package com.hapataka.questwalk.data.dto.dust

import com.google.gson.annotations.SerializedName

data class DustBody(
    @SerializedName("items")
    val dustItems: List<com.hapataka.questwalk.data.dto.dust.DustItem>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)