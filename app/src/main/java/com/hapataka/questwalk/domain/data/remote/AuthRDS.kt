package com.hapataka.questwalk.domain.data.remote

import com.google.firebase.auth.FirebaseUser

interface AuthRDS {
    suspend fun loginByEmailAndPw(email: String, pw: String): Result<Boolean>
    suspend fun getCurrentUserInfo(): FirebaseUser?
    suspend fun registerByEmailAndPw(email: String, pw: String): Result<Boolean>
    suspend fun logout(): Result<Unit>
    suspend fun reauthCurrentUser(pw: String): Result<Unit>
    suspend fun dropOutCurrentUser(): Result<Unit>
}