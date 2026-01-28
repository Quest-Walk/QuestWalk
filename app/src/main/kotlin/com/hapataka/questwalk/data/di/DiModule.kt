package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.data.repository.OcrRepositoryImpl
import com.hapataka.questwalk.domain.repository.OcrRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DiModule {
    @Binds
    abstract fun provideOcrRepository(ocrRepo: OcrRepositoryImpl): OcrRepository
}