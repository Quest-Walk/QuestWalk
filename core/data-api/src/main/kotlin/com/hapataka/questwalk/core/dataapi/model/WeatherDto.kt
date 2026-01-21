package com.hapataka.questwalk.core.dataapi.model

data class WeatherDto(
    val baseDate: String,
    val baseTime: String,
    val forecasts: List<ForecastDto>,
)

data class ForecastDto(
    val fcstDate: String,
    val fcstTime: String,
    val sky: String,
    val precipType: String,
    val temp: String,
)
