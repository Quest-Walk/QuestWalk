package com.hapataka.questwalk.data.local.pref.repository

import android.content.SharedPreferences
import com.hapataka.questwalk.domain.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val userPref: SharedPreferences
): PreferenceRepository {
    override fun saveUser(email: String) {
        TODO("Not yet implemented")
    }
}