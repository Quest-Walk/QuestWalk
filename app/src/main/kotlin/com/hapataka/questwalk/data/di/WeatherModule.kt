package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.core.remote.api.WeatherDataSource
import com.hapataka.questwalk.data.datasource.remote.WeatherDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherModule {
    @Binds
    @Singleton
    abstract fun bindsWeatherDataSource(impl: WeatherDataSourceImpl): WeatherDataSource
}
