package com.hapataka.questwalk.ui.fragment.weather

data class WeatherData(
    val fcstDate: String,
    val fcstTime: String,
    val sky: String,
    val precipType: String,
    val temp: String
)