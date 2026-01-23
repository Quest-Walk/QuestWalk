package com.hapataka.questwalk.core.local.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hapataka.questwalk.core.local.api.PreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_preferences"
)

class PreferencesDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PreferencesDataSource {

    override suspend fun setLastEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_EMAIL_KEY] = email
        }
    }

    override fun getLastEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_EMAIL_KEY]
        }
    }

    companion object {
        private val LAST_EMAIL_KEY = stringPreferencesKey("last_email")
    }
}
