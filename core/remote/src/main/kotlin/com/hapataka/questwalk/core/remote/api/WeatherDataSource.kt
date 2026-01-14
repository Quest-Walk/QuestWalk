package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.remote.model.WeatherDto
import com.hapataka.questwalk.core.remote.model.DustDto

interface WeatherDataSource {
    suspend fun getWeatherInfo(): List<WeatherDto>
    suspend fun getDustInfo(): DustDto
}
