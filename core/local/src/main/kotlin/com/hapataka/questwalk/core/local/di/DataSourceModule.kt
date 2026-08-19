package com.hapataka.questwalk.core.local.di

import com.hapataka.questwalk.core.local.api.LocalUserDataSource
import com.hapataka.questwalk.core.local.api.PreferencesDataSource
import com.hapataka.questwalk.core.local.datasource.LocalUserDataSourceImpl
import com.hapataka.questwalk.core.local.datasource.PreferencesDataSourceImpl
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
    abstract fun bindPreferencesDataSource(preferencesDataSource: PreferencesDataSourceImpl): PreferencesDataSource
}