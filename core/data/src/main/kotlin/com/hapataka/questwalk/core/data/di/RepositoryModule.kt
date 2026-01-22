package com.hapataka.questwalk.core.data.di

import com.hapataka.questwalk.core.data.repository.DefaultAchieveItemRepository
import com.hapataka.questwalk.core.data.repository.DefaultAuthRepository
import com.hapataka.questwalk.core.data.repository.DefaultHistoryRepository
import com.hapataka.questwalk.core.data.repository.DefaultLocationRepository
import com.hapataka.questwalk.core.data.repository.DefaultQuestRepository
import com.hapataka.questwalk.core.data.repository.DefaultUserRepository
import com.hapataka.questwalk.core.data.repository.DefaultWeatherRepository
import com.hapataka.questwalk.core.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.domain.repository.LocationRepository
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import com.hapataka.questwalk.core.domain.repository.WeatherRepository
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

    @Binds
    @Singleton
    abstract fun bindsHistoryRepository(historyRepository: DefaultHistoryRepository): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindsAchieveItemRepository(repository: DefaultAchieveItemRepository): AchieveItemRepository

    @Binds
    @Singleton
    abstract fun bindsQuestRepository(repository: DefaultQuestRepository): QuestRepository

    @Binds
    @Singleton
    abstract fun bindsWeatherRepository(repository: DefaultWeatherRepository): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindsLocationRepository(repository: DefaultLocationRepository): LocationRepository
}