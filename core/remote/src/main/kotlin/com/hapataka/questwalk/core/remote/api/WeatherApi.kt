package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.remote.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherApi {
    @GET("getVilageFcst")
    suspend fun getWeatherForecast(
        @QueryMap queries: Map<String, String>,
    ): WeatherResponse
}
