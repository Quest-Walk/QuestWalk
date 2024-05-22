package com.hapataka.questwalk.data.dto

data class WeatherResponse(
    val weather: Weather
)

data class Weather(
    val weatherBody: WeatherBody
)

data class WeatherBody(
    val dataType: String,
    val weatherItems: WeatherItems,
)

data class WeatherItems(
    val weatherItem: List<WeatherItem>
)

data class WeatherItem(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val nx: Int,
    val ny: Int
)