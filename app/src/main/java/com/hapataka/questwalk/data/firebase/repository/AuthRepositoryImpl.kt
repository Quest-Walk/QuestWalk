package com.hapataka.questwalk.data.firebase.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.hapataka.questwalk.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {
    private val auth by lazy { Firebase.auth }

    override suspend fun registerByEmailAndPw(
        email: String,
        pw: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task -> callback(task) }

    }

    override suspend fun loginByEmailAndPw(
        email: String,
        pw: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task -> callback(task) }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun deleteCurrentUser(
        callback: (Task<Void>) -> Unit
    ) {
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task -> callback(task) }
    }

    override suspend fun getCurrentUserUid(): String = auth.currentUser?.uid ?: ""
}