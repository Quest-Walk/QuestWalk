package com.hapataka.questwalk.core.remote.model.dust

import com.google.gson.annotations.SerializedName

data class DustApiResponse(
    @SerializedName("response")
    val response: DustResponse,
)

data class DustResponse(
    @SerializedName("header")
    val header: DustHeader,
    @SerializedName("body")
    val body: DustBody,
)

data class DustHeader(
    val resultCode: String,
    val resultMsg: String,
)

data class DustBody(
    @SerializedName("items")
    val items: List<DustItem>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
)

data class DustItem(
    val dataTime: String,
    val pm10Value: String?,
    val pm10Grade: String?,
    val pm25Value: String?,
    val pm25Grade: String?,
    val stationName: String,
    val sidoName: String,
)

// Station Response
data class StationApiResponse(
    val response: StationResponse,
)

data class StationResponse(
    val header: StationHeader,
    val body: StationBody,
)

data class StationHeader(
    val resultCode: String,
    val resultMsg: String,
)

data class StationBody(
    val items: List<StationItem>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
)

data class StationItem(
    val addr: String,
    val stationCode: String,
    val stationName: String,
    val tm: Double,
)
