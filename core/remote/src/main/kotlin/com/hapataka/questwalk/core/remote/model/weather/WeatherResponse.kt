package com.hapataka.questwalk.core.remote.model.weather

data class WeatherResponse(
    val response: WeatherResponseBody,
)

data class WeatherResponseBody(
    val header: WeatherHeader,
    val body: WeatherBody,
)

data class WeatherHeader(
    val resultCode: String,
    val resultMsg: String,
)

data class WeatherBody(
    val dataType: String,
    val items: WeatherItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
)

data class WeatherItems(
    val item: List<WeatherItem>,
)

data class WeatherItem(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val nx: Int,
    val ny: Int,
)
