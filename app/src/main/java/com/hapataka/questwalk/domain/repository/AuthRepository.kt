package com.hapataka.questwalk.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface AuthRepository {
    suspend fun registerByEmailAndPw(email: String, pw: String, callback: (Task<AuthResult>) -> Unit)
    suspend fun loginByEmailAndPw(email: String, pw: String, callback: (Task<AuthResult>) -> Unit)
    suspend fun deleteCurrentUser   (callback: (Task<Void>) -> Unit)
    suspend fun logout()
    suspend fun reauth(pw: String): Boolean
    suspend fun getCurrentUserUid(): String
    suspend fun getCurrentUserEmail(): String
}