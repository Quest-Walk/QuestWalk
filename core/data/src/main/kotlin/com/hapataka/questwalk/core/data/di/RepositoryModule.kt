package com.hapataka.questwalk.core.data.di

import com.hapataka.questwalk.core.data.repository.DefaultAuthRepository
import com.hapataka.questwalk.core.data.repository.DefaultUserRepository
import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Named("DefaultAuthRepository")
    @Singleton
    abstract fun bindsAuthRepository(authRepository: DefaultAuthRepository): AuthRepository

    @Binds
    @Named("DefaultUserRepository")
    @Singleton
    abstract fun bindsUserRepository(userRepository: DefaultUserRepository): UserRepositoryNew
}