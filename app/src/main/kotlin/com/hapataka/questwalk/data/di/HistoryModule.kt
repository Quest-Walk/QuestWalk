package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.datasource.remote.FirebaseHistoryRDS
import com.hapataka.questwalk.data.repository.HistoryRepositoryImpl
import com.hapataka.questwalk.domain.data.remote.HistoryRDS
import com.hapataka.questwalk.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryModule {
    @Binds
    @Named("FirebaseHistoryRDS")
    abstract fun provideHistoryRDS(historyRDS: FirebaseHistoryRDS): HistoryRDS

    @Binds
    abstract fun provideHistoryRepository(historyRepository: HistoryRepositoryImpl): HistoryRepository
}