package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.data.dto.DustDTO
import com.hapataka.questwalk.data.dto.ForecastDTO
import com.hapataka.questwalk.data.dto.HourlyForecast
import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.domain.repository.DustRemoteDataSource
import com.hapataka.questwalk.domain.repository.WeatherRemoteDataSource
import com.hapataka.questwalk.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val dustRemoteDataSource: DustRemoteDataSource
) : WeatherRepository {
    override suspend fun getWeatherInfo(currentLocation: Pair<Float, Float>): WeatherModel =
        withContext(Dispatchers.IO) {
            val weatherResponse = async { weatherRemoteDataSource.getWeatherInfo(currentLocation) }.await()
            val dustResponse = async { dustRemoteDataSource.getDustInfo(currentLocation) }.await()

            convertToWeatherModel(weatherResponse, dustResponse)
        }

    private fun convertToWeatherModel(
        forecastResponse: ForecastDTO,
        dustResponse: DustDTO
    ): WeatherModel {
        val requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH00")).toInt()
        val requestDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        val foreCastModelList = forecastResponse.hourlyForecast.groupBy { "${it.fcstDate}${it.fcstTime}" }
            .map { (_, weatherList) ->
                val sky = weatherList.first { it.category == "SKY" }.fcstValue
                val pty = weatherList.first { it.category == "PTY" }.fcstValue
                val tmp = weatherList.first { it.category == "TMP" }.fcstValue

                WeatherModel.ForecastModel(
                    fcstDate = weatherList.first().fcstDate,
                    fcstTime = weatherList.first().fcstTime,
                    baseDate = weatherList.first().baseDate,
                    sky = sky,
                    precipType = pty,
                    temp = tmp
                )
            }.filter { it.fcstTime.toInt() >= requestTime || it.fcstDate.toInt() > requestDay }.take(10)

        return WeatherModel(
            forecastList = foreCastModelList,
        ).apply {
            setPmInfo(dustResponse.pm10Value.toIntOrNull() ?: -1, dustResponse.pm25Value.toIntOrNull() ?: -1)
        }
    }
}

// PTY 강수 형태 (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
//             (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
// SKY 하늘 상태 0~5 맑음 6~8 구름 많음 9~10 흐림
// T1H 기온