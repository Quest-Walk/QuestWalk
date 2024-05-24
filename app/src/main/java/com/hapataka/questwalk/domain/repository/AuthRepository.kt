package com.hapataka.questwalk.domain.repository

interface AuthRepository {
    suspend fun registerByIdAndPw(email: String, pw: String)
    suspend fun loginByIdAndPw(email: String, pw: String)

    suspend fun getCurrentUserInfo()
}