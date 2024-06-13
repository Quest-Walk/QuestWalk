package com.hapataka.questwalk.data.model

//data class WeatherModel (
//    val fcstDate: String,
//    val fcstTime: String,
//    val baseDate: String,
//    val sky: String,
//    val precipType: String,
//    val temp: String,
//    val pm10Value: Int,
//    val pm10Grade: Int,
//    val pm25Value: Int,
//    val pm25Grade: Int
//)
data class WeatherModel(
    val forecastList: List<ForecastModel>,
    var pm10Value: Int = 0,
    var pm25Value: Int = 0,
    var pm10Grade: Int = 0,
    var pm25Grade: Int = 0,
) {
    fun setPmInfo(pm10Value: Int, pm25Value: Int) {
        this.pm10Value = pm10Value
        this.pm25Value = pm25Value
        pm10Grade = when (pm10Value) {
            in 0..12 -> 1
            in 13..35 ->2
            in 36 .. Int.MAX_VALUE -> 3
            else -> throw IllegalArgumentException("PM10 값이 유효하지 않습니다.")
        }
        pm25Grade = when (pm25Value) {
            in 0..15 -> 1
            in 16..35 -> 2
            in 36..Int.MAX_VALUE -> 3
            else -> throw IllegalArgumentException("PM25 값이 유효하지 않습니다.")
        }
    }
    data class ForecastModel (
        val fcstDate: String,
        val fcstTime: String,
        val baseDate: String,
        val sky: String,
        val precipType: String,
        val temp: String,
    )
}

