package com.hapataka.questwalk.core.remote.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.remote.api.AchieveItemDataSource
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import com.hapataka.questwalk.core.remote.api.HistoryDataSource
import com.hapataka.questwalk.core.remote.api.UserDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseAchieveItemDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseAuthDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseHistoryDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {
    private val firebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Named("FirebaseAuth")
    @Singleton
    fun providesAuthDataSource(): AuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth)
    }

    @Provides
    @Named("FirestoreUser")
    @Singleton
    fun providesUserDataSource(): UserDataSource {
        return FirebaseUserDataSource(
            FirebaseFirestore.getInstance(),
            FirebaseFirestore.getInstance().collection("users"),
        )
    }

    @Provides
    @Named("FirestoreHistory")
    @Singleton
    fun providesHistoryDataSource(): HistoryDataSource {
        return FirebaseHistoryDataSource(FirebaseFirestore.getInstance().collection("histories"))
    }

    @Provides
    @Singleton
    fun providesAchieveItemDataSource(): AchieveItemDataSource {
        return FirebaseAchieveItemDataSource(FirebaseFirestore.getInstance())
    }
}