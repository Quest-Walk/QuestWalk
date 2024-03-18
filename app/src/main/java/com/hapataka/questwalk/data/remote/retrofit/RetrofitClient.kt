package com.hapataka.questwalk.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    private const val WEATHER_BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
    private const val DUST_BASE_URL = "https://apis.data.go.kr/B552584/"
    val weatherApi: WeatherService by lazy { WeatherRetrofit.create(WeatherService::class.java) }
    val dustApi: DustService by lazy { dustRetrofit.create(DustService::class.java) }

    private fun createOKHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private val WeatherRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(createOKHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val dustRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DUST_BASE_URL)
            .client(createOKHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}