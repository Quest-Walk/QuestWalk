package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.WeatherRepository
import com.hapataka.questwalk.core.model.Dust
import com.hapataka.questwalk.core.model.Weather
import com.hapataka.questwalk.core.remote.api.WeatherDataSource
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherDataSource: WeatherDataSource,
) : WeatherRepository {

    override suspend fun getWeatherInfo(): Result<List<Weather>> {
        return runCatching {
            weatherDataSource.getWeatherInfo().map { dto ->
                Weather(
                    fcstDate = dto.fcstDate,
                    fcstTime = dto.fcstTime,
                    sky = dto.sky,
                    precipType = dto.precipType,
                    temp = dto.temp,
                )
            }
        }
    }

    override suspend fun getDustInfo(): Result<Dust> {
        return runCatching {
            val dto = weatherDataSource.getDustInfo()
            Dust(
                pm10Value = dto.pm10Value,
                pm25Value = dto.pm25Value,
            )
        }
    }
}
