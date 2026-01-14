package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Dust
import com.hapataka.questwalk.core.model.Weather

interface WeatherRepository {
    suspend fun getWeatherInfo(): Result<List<Weather>>
    suspend fun getDustInfo(): Result<Dust>
}
