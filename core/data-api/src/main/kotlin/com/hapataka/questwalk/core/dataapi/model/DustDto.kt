package com.hapataka.questwalk.core.dataapi.model

data class DustDto(
    val pm10Value: Int,
    val pm25Value: Int,
    val stationName: String,
    val stationAddress: String,
)
