package com.hapataka.questwalk.data.remote.retrofit

import com.hapataka.questwalk.data.remote.dto.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherService {
    @GET("getVilageFcst")
    suspend fun getWeatherInfo(
        @QueryMap queries: Map<String, String>
    ): WeatherResponse

}