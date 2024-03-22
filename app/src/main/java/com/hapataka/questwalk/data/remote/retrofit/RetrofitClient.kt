package com.hapataka.questwalk.data.remote.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val WEATHER_BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
    private const val DUST_BASE_URL = "https://apis.data.go.kr/B552584/"
    val weatherApi: WeatherService by lazy { WeatherRetrofit.create(WeatherService::class.java) }
    val dustApi: DustService by lazy { dustRetrofit.create(DustService::class.java) }
    val gson = GsonBuilder().setLenient().create()

    private fun createOKHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    private val WeatherRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(createOKHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val dustRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DUST_BASE_URL)
            .client(createOKHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}