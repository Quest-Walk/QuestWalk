package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.datasource.local.CacheDataSourceImpl
import com.hapataka.questwalk.data.datasource.remote.FirebaseUserRDSImpl
import com.hapataka.questwalk.data.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.repository.UserRepoImpl
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import com.hapataka.questwalk.domain.data.remote.UserRDS
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Binds
    abstract fun provideImageRepository(imageRepo: ImageRepositoryImpl): ImageRepository

    @Binds
    abstract fun provideUserRepository(userRepoImpl: UserRepoImpl): UserRepo

    @Binds
    abstract fun provideUserRDS(userRDS: FirebaseUserRDSImpl): UserRDS

    @Binds
    abstract fun provideCacheDataSource(cacheDataSource: CacheDataSourceImpl): CacheDataSource

}