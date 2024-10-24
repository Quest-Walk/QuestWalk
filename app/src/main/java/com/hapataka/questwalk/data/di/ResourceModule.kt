package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.datasource.remote.FirebaseAchievementsDataSource
import com.hapataka.questwalk.domain.data.remote.AchievementsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {
    @Binds
    @Named("FirebaseAchievementsDataSource")
    abstract fun provideAchievementsDataSource(datasource: FirebaseAchievementsDataSource): AchievementsDataSource
}