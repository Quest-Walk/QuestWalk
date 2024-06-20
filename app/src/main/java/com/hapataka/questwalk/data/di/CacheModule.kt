package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.datasource.local.CacheDataSourceImpl
import com.hapataka.questwalk.data.datasource.local.PrefDataSourceImpl
import com.hapataka.questwalk.data.repository.CacheRepositoryImpl
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import com.hapataka.questwalk.domain.repository.CacheRepository
import com.hapataka.questwalk.domain.repository.PrefDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {
    @Binds
    abstract fun provideCacheRepository(cacheRepository: CacheRepositoryImpl): CacheRepository

    @Binds
    @Singleton
    abstract fun provideCacheDataSource(cacheDataSource: CacheDataSourceImpl): CacheDataSource

    @Binds
    abstract fun providePrefDataSource(prefDataSource: PrefDataSourceImpl): PrefDataSource

}