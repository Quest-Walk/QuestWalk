package com.hapataka.questwalk.core.remote.model

data class WeatherDto(
    val fcstDate: String,
    val fcstTime: String,
    val sky: String,
    val precipType: String,
    val temp: String,
)
