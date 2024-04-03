package com.hapataka.questwalk.di

import com.hapataka.questwalk.data.cloudvision.repository.OcrRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.AchieveItemRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.EncryptionKeyRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.data.fusedlocation.repository.LocationRepositoryImpl
import com.hapataka.questwalk.data.pref.repository.LocalRepositoryImpl
import com.hapataka.questwalk.data.remote.repository.DustRepositoryImpl
import com.hapataka.questwalk.data.remote.repository.WeatherRepositoryImpl
import com.hapataka.questwalk.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.repository.EncryptionKeyRepository
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.LocalRepository
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.OcrRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepository
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
    abstract fun provideAuthRepository(authRepoImpl: AuthRepositoryImpl):AuthRepository

    @Binds
    abstract fun provideUserRepository(userRepoImpl: UserRepositoryImpl):UserRepository

    @Binds
    abstract fun provideAchievementItemRepository(achieveItemRepo: AchieveItemRepositoryImpl): AchieveItemRepository

    @Binds
    abstract fun provideQuestRepository(questRepo: QuestStackRepositoryImpl): QuestStackRepository

    @Binds
    abstract fun provideLocalRepository(localRepo: LocalRepositoryImpl): LocalRepository

    @Binds
    abstract fun provideLocationRepository(locationRepo: LocationRepositoryImpl): LocationRepository

    @Binds
    abstract fun provideImageRepository(imageRepo: ImageRepositoryImpl): ImageRepository
}