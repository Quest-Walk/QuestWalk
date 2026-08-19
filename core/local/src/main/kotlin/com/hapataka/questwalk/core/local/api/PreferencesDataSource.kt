package com.hapataka.questwalk.core.local.api

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    suspend fun setLastEmail(email: String)
    fun getLastEmail(): Flow<String?>
}
