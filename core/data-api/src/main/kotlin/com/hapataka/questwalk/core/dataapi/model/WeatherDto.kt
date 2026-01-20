package com.hapataka.questwalk.core.dataapi.model

data class WeatherDto(
    val baseDate: String,
    val baseTime: String,
    val fcstDate: String,
    val fcstTime: String,
    val sky: String,
    val precipType: String,
    val temp: String,
)
