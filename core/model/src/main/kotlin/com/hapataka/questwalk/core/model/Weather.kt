package com.hapataka.questwalk.core.model

data class Weather(
    val baseDate: String,
    val baseTime: String,
    val current: Forecast,
    val forecasts: List<Forecast>,
    val pm10: Int,
    val pm25: Int,
    val region: Region,
)

data class Forecast(
    val fcstDate: String,
    val fcstTime: String,
    val sky: SkyType,
    val precipType: PrecipType,
    val temp: Int,
)

data class Region(
    val name: String,
    val address: String,
)

enum class SkyType {
    CLEAR,      // 맑음
    CLOUDY,     // 구름많음
    OVERCAST,   // 흐림
}

enum class PrecipType {
    NONE,       // 없음
    RAIN,       // 비
    SNOW,       // 눈
    RAIN_SNOW,  // 비/눈
    SHOWER,     // 소나기
}
