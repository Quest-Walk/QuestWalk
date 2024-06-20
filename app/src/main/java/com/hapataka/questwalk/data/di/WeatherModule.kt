package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.datasource.remote.DustRemoteDataSourceImpl
import com.hapataka.questwalk.data.datasource.remote.WeatherRemoteDataSourceImpl
import com.hapataka.questwalk.data.repository.WeatherRepositoryImpl
import com.hapataka.questwalk.domain.repository.DustRemoteDataSource
import com.hapataka.questwalk.domain.repository.WeatherRemoteDataSource
import com.hapataka.questwalk.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class WeatherModule {
    @Binds
    abstract fun bindDustRemoteDataSource(dustRemoteDataSourceImpl: DustRemoteDataSourceImpl): DustRemoteDataSource

    @Binds
    abstract fun bindWeatherRemoteDataSource(weatherRemoteDataSourceImpl: WeatherRemoteDataSourceImpl): WeatherRemoteDataSource

    @Binds
    abstract fun bindWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository
}