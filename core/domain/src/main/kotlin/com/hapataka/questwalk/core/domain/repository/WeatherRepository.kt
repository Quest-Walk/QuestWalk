package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Weather

interface WeatherRepository {
    suspend fun getWeatherInfo(): Result<Weather>
}
