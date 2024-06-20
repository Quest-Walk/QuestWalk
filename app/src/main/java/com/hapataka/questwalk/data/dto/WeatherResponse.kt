package com.hapataka.questwalk.data.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("response")
    val weather: Weather
)

data class Weather(
    @SerializedName("body")
    val weatherBody: WeatherBody
)

data class WeatherBody(
    val dataType: String,
    @SerializedName("items")
    val forecastDTO: ForecastDTO,
)

data class ForecastDTO(
    @SerializedName("item")
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