package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {

    @Provides
    @Named("weatherApiKey")
    fun provideWeatherApiKey(): String {
        return BuildConfig.weather_key
    }
}
