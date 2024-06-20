package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.WeatherModel

interface WeatherRepository {
    suspend fun getWeatherInfo(currentLocation: Pair<Float, Float>): Result<WeatherModel>
}