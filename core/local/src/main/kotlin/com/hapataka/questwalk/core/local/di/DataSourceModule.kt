package com.hapataka.questwalk.core.local.di

import com.hapataka.questwalk.core.local.api.LocalUserDataSource
import com.hapataka.questwalk.core.local.datasource.LocalUserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindUserDataSource(localUserDataSource: LocalUserDataSourceImpl): LocalUserDataSource
}