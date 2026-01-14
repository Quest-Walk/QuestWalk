package com.hapataka.questwalk.core.model

data class Weather(
    val fcstDate: String,
    val fcstTime: String,
    val sky: String,
    val precipType: String,
    val temp: String,
)
