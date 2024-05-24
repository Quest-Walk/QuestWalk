package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.datasource.remote.FirebaseAuthRDSImpl
import com.hapataka.questwalk.data.repository.AuthRepositoryImpl
import com.hapataka.questwalk.domain.data.remote.AuthRDS
import com.hapataka.questwalk.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn (SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Named("firebaseAuth")
    abstract fun provideAuthRDS(firebaseAuthRDS: FirebaseAuthRDSImpl) : AuthRDS

    @Binds
    abstract fun provideAuthRepo(authRepositoryImpl: AuthRepositoryImpl) : AuthRepository

}