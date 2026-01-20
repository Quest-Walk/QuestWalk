package com.hapataka.questwalk.core.local.di

import com.hapataka.questwalk.core.dataapi.datasource.LocationDataSource
import com.hapataka.questwalk.core.local.api.LocalUserDataSource
import com.hapataka.questwalk.core.local.datasource.FusedLocationDataSource
import com.hapataka.questwalk.core.local.datasource.LocalUserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindUserDataSource(localUserDataSource: LocalUserDataSourceImpl): LocalUserDataSource

    @Binds
    @Singleton
    abstract fun bindLocationDataSource(impl: FusedLocationDataSource): LocationDataSource
}