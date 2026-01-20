package com.hapataka.questwalk.core.dataapi.datasource

interface AuthRemoteDataSource {
    suspend fun loginWithEmail(email: String, password: String): Result<String>
    suspend fun joinWithEmail(email: String, password: String): Result<String>
    suspend fun loginWithGoogle(idToken: String): Result<String>
    fun getUserId(): String?
    fun logout()
}
