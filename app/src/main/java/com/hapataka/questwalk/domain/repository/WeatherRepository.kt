package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.WeatherModel

interface WeatherRepository {

    //    suspend fun getWeatherInfo(queries: Map<String, String>): MutableList<WeatherEntity>
    suspend fun getWeatherInfo(currentLocation: Pair<Float, Float>): WeatherModel
}