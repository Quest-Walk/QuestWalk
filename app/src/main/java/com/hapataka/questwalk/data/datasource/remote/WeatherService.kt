package com.hapataka.questwalk.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherService {
    @GET("getVilageFcst")
    suspend fun getWeatherInfo(
        @QueryMap queries: Map<String, String>
    ): com.hapataka.questwalk.data.dto.weather.WeatherResponse

}