package com.hapataka.questwalk.data.dto

import com.google.gson.annotations.SerializedName

data class DustResponse(
    @SerializedName("response")
    val dust: Dust
)

data class Dust(
    @SerializedName("body")
    val dustBody: DustBody
)

data class DustBody(
    @SerializedName("items")
    val dustDTO: List<DustDTO>,
)

// TODO:  DTO
data class DustDTO(
    val pm10Flag: Any,
    val pm10Grade: String,
    val pm10Value: String,
    val pm25Flag: Any,
    val pm25Grade: String,
    val pm25Value: String,
)