package com.hapataka.questwalk.core.remote.di

import com.google.firebase.auth.FirebaseAuth
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseAuthDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {
    @Provides
    @Named("Firebase")
    @Singleton
    fun providesAuthDataSource(): AuthDataSource {
        return FirebaseAuthDataSource(FirebaseAuth.getInstance())
    }
}