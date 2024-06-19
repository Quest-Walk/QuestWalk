package com.hapataka.questwalk.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hapataka.questwalk.domain.repository.PrefDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val USER_PREFERENCES_NAME = "user_preferences"
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class PrefDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefDataSource {
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    override suspend fun setUserId(id: String) {
        context.userDataStore.edit { pref ->
            pref[USER_ID_KEY] = id
        }
    }

    override suspend fun getUserId(): Flow<String?> {
        return context.userDataStore.data.map { pref -> pref[USER_ID_KEY] }
    }
}