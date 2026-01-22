package com.hapataka.questwalk.core.remote.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.dataapi.datasource.DustRemoteDataSource
import com.hapataka.questwalk.core.dataapi.datasource.LocationDataSource
import com.hapataka.questwalk.core.dataapi.datasource.WeatherRemoteDataSource
import com.hapataka.questwalk.core.remote.api.AchieveItemDataSource
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import com.hapataka.questwalk.core.remote.api.DustApi
import com.hapataka.questwalk.core.dataapi.datasource.HistoryRemoteDataSource
import com.hapataka.questwalk.core.dataapi.datasource.QuestRemoteDataSource
import com.hapataka.questwalk.core.remote.api.UserDataSource
import com.hapataka.questwalk.core.remote.api.WeatherApi
import com.hapataka.questwalk.core.remote.datasource.FirebaseAchieveItemDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseAuthDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseHistoryDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseQuestDataSource
import com.hapataka.questwalk.core.remote.datasource.FirebaseUserDataSource
import com.hapataka.questwalk.core.remote.datasource.FusedLocationDataSource
import com.hapataka.questwalk.core.remote.datasource.HttpDustDataSource
import com.hapataka.questwalk.core.remote.datasource.HttpWeatherDataSource
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
    @Singleton
    fun providesHistoryRemoteDataSource(): HistoryRemoteDataSource {
        return FirebaseHistoryDataSource(FirebaseFirestore.getInstance().collection("histories"))
    }

    @Provides
    @Singleton
    fun providesAchieveItemDataSource(): AchieveItemDataSource {
        return FirebaseAchieveItemDataSource(FirebaseFirestore.getInstance())
    }

    @Provides
    @Singleton
    fun providesQuestRemoteDataSource(): QuestRemoteDataSource {
        return FirebaseQuestDataSource(FirebaseFirestore.getInstance())
    }

    @Provides
    @Singleton
    fun providesWeatherRemoteDataSource(
        weatherApi: WeatherApi,
        @Named("weatherApiKey") apiKey: String,
    ): WeatherRemoteDataSource {
        return HttpWeatherDataSource(weatherApi, apiKey)
    }

    @Provides
    @Singleton
    fun providesDustRemoteDataSource(
        dustApi: DustApi,
        @Named("weatherApiKey") apiKey: String,
    ): DustRemoteDataSource {
        return HttpDustDataSource(dustApi, apiKey)
    }

    @Provides
    @Singleton
    fun providesLocationDataSource(
        fusedLocationDataSource: FusedLocationDataSource,
    ): LocationDataSource {
        return fusedLocationDataSource
    }
}