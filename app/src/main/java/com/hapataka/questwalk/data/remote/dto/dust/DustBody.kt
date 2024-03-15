package com.hapataka.questwalk.data.remote.dto.dust

import com.google.gson.annotations.SerializedName

data class DustBody(
    @SerializedName("items")
    val dustItems: List<DustItem>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)