package com.hapataka.questwalk.data.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val weather: Weather
)

data class Weather(
    val weatherBody: WeatherBody
)

data class WeatherBody(
    val dataType: String,
    @SerializedName("weatherItems")
    val forecastDTO: ForecastDTO,
)

data class ForecastDTO(
    @SerializedName("weatherDTO")
    val hourlyForecast: List<HourlyForecast>
)

data class HourlyForecast(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val nx: Int,
    val ny: Int
)