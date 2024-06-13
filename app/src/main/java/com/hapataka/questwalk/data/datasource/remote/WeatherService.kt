package com.hapataka.questwalk.data.datasource.remote

import com.hapataka.questwalk.data.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherService {
    @GET("getVilageFcst")
    suspend fun getWeatherInfo(
        @QueryMap queryMap: Map<String, String>
    ): WeatherResponse

}