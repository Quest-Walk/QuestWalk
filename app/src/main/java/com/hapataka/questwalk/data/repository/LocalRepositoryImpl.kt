package com.hapataka.questwalk.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.hapataka.questwalk.domain.repository.LocalRepository
import com.hapataka.questwalk.ui.fragment.result.USER_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) : LocalRepository {
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