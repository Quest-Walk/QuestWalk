package com.hapataka.questwalk.domain.repository

interface AuthRepository {
    suspend fun registerByIdAndPw(email: String, pw: String): Result<Boolean>
    suspend fun loginByIdAndPw(email: String, pw: String): Result<Boolean>
    suspend fun getCurrentUserId(): String?
}