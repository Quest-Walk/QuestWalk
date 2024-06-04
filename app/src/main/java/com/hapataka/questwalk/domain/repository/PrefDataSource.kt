package com.hapataka.questwalk.domain.repository

import kotlinx.coroutines.flow.Flow

interface PrefDataSource {
        suspend fun setUserId(id: String)
        suspend fun getUserId(): Flow<String?>
}