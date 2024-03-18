package com.hapataka.questwalk.data.pref.repository

import android.content.Context
import android.content.SharedPreferences
import com.hapataka.questwalk.domain.repository.LocalRepository
import com.hapataka.questwalk.ui.result.USER_ID

class LocalRepositoryImpl(val context: Context) : LocalRepository {
    private val pref: SharedPreferences = context.getSharedPreferences(USER_ID, 0)
    override fun setUserId(id: String) {
        pref.edit().run {
            putString(USER_ID, id)
            apply()
        }
    }

    override fun getUserId(): String {
        return pref.getString(USER_ID, "") ?: ""
    }
}