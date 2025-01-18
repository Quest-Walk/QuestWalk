package com.hapataka.questwalk.core.remote.api

interface AuthDataSource {
    suspend fun loginWithEmail(email: String, password: String): Result<String>
    fun getUserId(): String
    suspend fun loginWithGoogle(idToken: String): Result<String>
    suspend fun joinWithEmail(email: String, password: String): Result<String>
    fun logout()
}