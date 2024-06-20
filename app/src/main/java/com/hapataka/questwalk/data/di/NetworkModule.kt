package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.retrofit.DustService
import com.hapataka.questwalk.data.retrofit.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("weatherRetrofit")
    fun provideWeatherRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("dustRetrofit")
    fun provideDustRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val DUST_BASE_URL = "https://apis.data.go.kr/B552584/"
        return Retrofit.Builder()
            .baseUrl(DUST_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(@Named("weatherRetrofit") retrofit: Retrofit): WeatherService = retrofit.create(
        WeatherService::class.java)

    @Provides
    @Singleton
    fun provideDustService(@Named("dustRetrofit") retrofit: Retrofit): DustService = retrofit.create(
        DustService::class.java)

}