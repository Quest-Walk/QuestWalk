package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.WeatherEntity

interface WeatherRepository {

    suspend fun getWeatherInfo(queries: Map<String, String>): MutableList<WeatherEntity>
}