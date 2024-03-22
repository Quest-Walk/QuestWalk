package com.hapataka.questwalk.di

import android.content.Context
import com.hapataka.questwalk.data.remote.retrofit.WeatherService
import com.hapataka.questwalk.ui.camera.CameraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
//    @Provides
//    @Singleton
//    fun provideOkHttpClient(): OkHttpClient {
//        val interceptor = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//        return OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideWeatherRetrofit(
//        okHttpClient: OkHttpClient,
//    ): Retrofit {
//        val BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideWeatherService(retrofit: Retrofit): WeatherService = retrofit.create(WeatherService::class.java)

    @Provides
    fun provideCameraRepository(@ApplicationContext context: Context): CameraRepository {
        return CameraRepository(context)
    }

}