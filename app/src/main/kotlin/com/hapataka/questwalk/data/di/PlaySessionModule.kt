package com.hapataka.questwalk.data.di

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.data.repository.DefaultPlaySessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaySessionModule {

    @Binds
    @Singleton
    abstract fun bindPlaySessionRepository(
        repository: DefaultPlaySessionRepository
    ): PlaySessionRepository

    companion object {
        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }
    }
}
