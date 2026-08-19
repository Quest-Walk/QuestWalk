package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.dataapi.datasource.DustRemoteDataSource
import com.hapataka.questwalk.core.dataapi.datasource.LocationDataSource
import com.hapataka.questwalk.core.dataapi.datasource.WeatherRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.DustDto
import com.hapataka.questwalk.core.dataapi.model.ForecastDto
import com.hapataka.questwalk.core.dataapi.model.WeatherDto
import com.hapataka.questwalk.core.domain.repository.WeatherRepository
import com.hapataka.questwalk.core.model.Forecast
import com.hapataka.questwalk.core.model.PrecipType
import com.hapataka.questwalk.core.model.Region
import com.hapataka.questwalk.core.model.SkyType
import com.hapataka.questwalk.core.model.Weather
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val dustRemoteDataSource: DustRemoteDataSource,
) : WeatherRepository {

    override suspend fun getWeatherInfo(): Result<Weather> = runCatching {
        val location = locationDataSource.getCurrentLocation()
            ?: throw LocationNotFoundException()

        coroutineScope {
            val weatherDeferred = async { weatherRemoteDataSource.getWeather(location) }
            val dustDeferred = async { dustRemoteDataSource.getDust(location) }

            val weatherDto = weatherDeferred.await().getOrElse { defaultWeatherDto() }
            val dustDto = dustDeferred.await().getOrElse { defaultDustDto() }

            val forecasts = weatherDto.forecasts.map { it.toForecast() }
            val current = forecasts.firstOrNull() ?: defaultForecast()

            Weather(
                baseDate = weatherDto.baseDate,
                baseTime = weatherDto.baseTime,
                current = current,
                forecasts = forecasts,
                pm10 = dustDto.pm10Value,
                pm25 = dustDto.pm25Value,
                region = Region(
                    name = dustDto.stationName,
                    address = dustDto.stationAddress,
                ),
            )
        }
    }

    private fun ForecastDto.toForecast(): Forecast {
        return Forecast(
            fcstDate = fcstDate,
            fcstTime = fcstTime,
            sky = parseSkyType(sky),
            precipType = parsePrecipType(precipType),
            temp = temp.toIntOrNull() ?: TEMP_UNAVAILABLE,
        )
    }

    private fun defaultWeatherDto(): WeatherDto {
        val now = LocalDateTime.now()
        val date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val time = now.format(DateTimeFormatter.ofPattern("HH00"))
        return WeatherDto(
            baseDate = date,
            baseTime = time,
            forecasts = listOf(
                ForecastDto(
                    fcstDate = date,
                    fcstTime = time,
                    sky = "-1",
                    precipType = "-1",
                    temp = TEMP_UNAVAILABLE.toString(),
                )
            ),
        )
    }

    private fun defaultForecast(): Forecast {
        val now = LocalDateTime.now()
        return Forecast(
            fcstDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            fcstTime = now.format(DateTimeFormatter.ofPattern("HH00")),
            sky = SkyType.CLEAR,
            precipType = PrecipType.NONE,
            temp = TEMP_UNAVAILABLE,
        )
    }

    private fun defaultDustDto(): DustDto {
        return DustDto(
            pm10Value = -1,
            pm25Value = -1,
            stationName = "-",
            stationAddress = "-",
        )
    }

    private fun parseSkyType(value: String): SkyType {
        return when (value) {
            "1" -> SkyType.CLEAR
            "3" -> SkyType.CLOUDY
            "4" -> SkyType.OVERCAST
            else -> SkyType.CLEAR
        }
    }

    private fun parsePrecipType(value: String): PrecipType {
        return when (value) {
            "0" -> PrecipType.NONE
            "1" -> PrecipType.RAIN
            "2" -> PrecipType.RAIN_SNOW
            "3" -> PrecipType.SNOW
            "4" -> PrecipType.SHOWER
            else -> PrecipType.NONE
        }
    }

    companion object {
        const val TEMP_UNAVAILABLE = -99
    }
}

class LocationNotFoundException : Exception("위치를 가져올 수 없습니다")
