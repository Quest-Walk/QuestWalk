package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.dataapi.datasource.DustRemoteDataSource
import com.hapataka.questwalk.core.dataapi.datasource.LocationDataSource
import com.hapataka.questwalk.core.dataapi.datasource.WeatherRemoteDataSource
import com.hapataka.questwalk.core.domain.repository.WeatherRepository
import com.hapataka.questwalk.core.model.PrecipType
import com.hapataka.questwalk.core.model.Region
import com.hapataka.questwalk.core.model.SkyType
import com.hapataka.questwalk.core.model.Weather
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val dustRemoteDataSource: DustRemoteDataSource,
) : WeatherRepository {

    override suspend fun getWeatherInfo(): Result<Weather> = runCatching {
        val location = locationDataSource.getCurrentLocation()
            ?: throw Exception("위치를 가져올 수 없습니다")

        coroutineScope {
            val weatherDeferred = async { weatherRemoteDataSource.getWeather(location) }
            val dustDeferred = async { dustRemoteDataSource.getDust(location) }

            val weatherDto = weatherDeferred.await().getOrThrow()
            val dustDto = dustDeferred.await().getOrThrow()

            Weather(
                baseDate = weatherDto.baseDate,
                baseTime = weatherDto.baseTime,
                fcstDate = weatherDto.fcstDate,
                fcstTime = weatherDto.fcstTime,
                sky = parseSkyType(weatherDto.sky),
                precipType = parsePrecipType(weatherDto.precipType),
                temp = weatherDto.temp.toIntOrNull() ?: 0,
                pm10 = dustDto.pm10Value,
                pm25 = dustDto.pm25Value,
                region = Region(
                    name = dustDto.stationName,
                    address = dustDto.stationAddress,
                ),
            )
        }
    }

    private fun parseSkyType(value: String): SkyType {
        return when (value) {
            "1" -> SkyType.CLEAR      // 맑음
            "3" -> SkyType.CLOUDY     // 구름많음
            "4" -> SkyType.OVERCAST   // 흐림
            else -> SkyType.CLEAR
        }
    }

    private fun parsePrecipType(value: String): PrecipType {
        return when (value) {
            "0" -> PrecipType.NONE       // 없음
            "1" -> PrecipType.RAIN       // 비
            "2" -> PrecipType.RAIN_SNOW  // 비/눈
            "3" -> PrecipType.SNOW       // 눈
            "4" -> PrecipType.SHOWER     // 소나기
            else -> PrecipType.NONE
        }
    }
}
