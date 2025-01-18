package com.hapataka.questwalk.core.domain.repository

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<String>
    fun getUserId(): String
    suspend fun loginWithGoogle(idToken: String): Result<String>
    suspend fun joinWithEmail(email: String, password: String): Result<String>
    fun logout()
}