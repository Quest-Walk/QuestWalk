package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.repository.UserRDSImpl
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.UserRDS
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
    abstract fun provideUserRepository(userRepoImpl: UserRDSImpl): UserRDS

}