package com.hapataka.questwalk.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApi {
    val weatherApi: WeatherService by lazy { retrofit.create(WeatherService::class.java) }
    private val BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

    private fun createOKHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOKHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}