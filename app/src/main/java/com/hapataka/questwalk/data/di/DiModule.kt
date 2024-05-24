package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.repository.AchieveItemRepositoryImpl
import com.hapataka.questwalk.data.repository.DustRepositoryImpl
import com.hapataka.questwalk.data.repository.EncryptionKeyRepositoryImpl
import com.hapataka.questwalk.data.repository.LocalRepositoryImpl
import com.hapataka.questwalk.data.repository.LocationRepositoryImpl
import com.hapataka.questwalk.data.repository.OcrRepositoryImpl
import com.hapataka.questwalk.data.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.repository.WeatherRepositoryImpl
import com.hapataka.questwalk.data.repository.backup.AuthRepoImpl
import com.hapataka.questwalk.domain.data.remote.EncryptionKeyRepository
import com.hapataka.questwalk.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.domain.repository.AuthRepo
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.repository.LocalRepository
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.OcrRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DiModule {
    @Binds
    abstract fun provideOcrRepository(ocrRepo: OcrRepositoryImpl): OcrRepository

    @Binds
    abstract fun provideEncryptRepository(encryptRepo: EncryptionKeyRepositoryImpl): EncryptionKeyRepository

    @Binds
    abstract fun provideWeatherRepository(weatherRepo: WeatherRepositoryImpl): WeatherRepository

    @Binds
    abstract fun provideDustRepository(dustRepo: DustRepositoryImpl): DustRepository

    @Binds
    abstract fun provideAuthRepository(authRepoImpl: AuthRepoImpl):AuthRepo

    @Binds
    abstract fun provideAchievementItemRepository(achieveItemRepo: AchieveItemRepositoryImpl): AchieveItemRepository

    @Binds
    abstract fun provideQuestRepository(questRepo: QuestStackRepositoryImpl): QuestStackRepository

    @Binds
    abstract fun provideLocalRepository(localRepo: LocalRepositoryImpl): LocalRepository

    @Binds
    abstract fun provideLocationRepository(locationRepo: LocationRepositoryImpl): LocationRepository
}