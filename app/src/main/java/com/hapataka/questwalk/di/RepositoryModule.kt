package com.hapataka.questwalk.di

import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.data.remote.repository.WeatherRepositoryImpl
import com.hapataka.questwalk.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWeatherRepository(repository: WeatherRepositoryImpl): WeatherRepository
}