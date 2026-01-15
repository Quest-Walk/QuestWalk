package com.hapataka.questwalk.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.hapataka.questwalk.domain.repository.LocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREF_KEY_USER_ID = "user_id"

class LocalRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    LocalRepository {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_KEY_USER_ID, 0)
    override fun setUserId(id: String) {
        pref.edit().run {
            putString(PREF_KEY_USER_ID, id)
            apply()
        }
    }

    override fun getUserId(): String {
        return pref.getString(PREF_KEY_USER_ID, "") ?: ""
    }
}