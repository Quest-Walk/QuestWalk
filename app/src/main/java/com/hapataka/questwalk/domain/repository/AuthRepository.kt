package com.hapataka.questwalk.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun registerByEmailAndPw(email: String, pw: String, callback: (Task<AuthResult>) -> Unit)
    suspend fun loginByEmailAndPw(email: String, pw: String, callback: (Task<AuthResult>) -> Unit)
    suspend fun deleteCurrentUser(currentUser: FirebaseUser, callback: (Task<Void>) -> Unit)

    suspend fun getCurrentUserUid()
}