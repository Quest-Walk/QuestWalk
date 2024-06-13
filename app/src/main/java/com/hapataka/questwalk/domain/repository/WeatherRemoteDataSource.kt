package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.dto.ForecastDTO
import com.hapataka.questwalk.data.dto.HourlyForecast

interface WeatherRemoteDataSource {
    suspend fun getWeatherInfo(currentLocation: Pair<Float, Float>): ForecastDTO
}