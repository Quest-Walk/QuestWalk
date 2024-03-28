package com.hapataka.questwalk.domain.entity

data class WeatherEntity(
    val fcstDate: String,
    val fcstTime: String,
    val baseDate: String,
    val sky: String,
    val precipType: String,
    val temp: String
)
