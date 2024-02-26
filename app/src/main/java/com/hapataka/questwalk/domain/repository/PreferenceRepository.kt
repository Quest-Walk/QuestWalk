package com.hapataka.questwalk.domain.repository

interface PreferenceRepository {
    fun saveUser(email:String) // pref에 현재 유저 저장 -> 자동로그인?
}