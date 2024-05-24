package com.hapataka.questwalk.data.repository.backup

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.hapataka.questwalk.domain.repository.AuthRepo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepoImpl @Inject constructor() : AuthRepo {
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

    override suspend fun deleteCurrentUser(callback: (Task<Void>) -> Unit) {
        val user = auth.currentUser

        user?.let {
            it.delete()
                .addOnCompleteListener { task -> callback(task) }
        }
    }

    override suspend fun reauth(pw: String): Boolean {
        val email = auth.currentUser?.email ?: ""
        val credential = EmailAuthProvider.getCredential(email, pw)

        Log.i("Authentication", "password: ${pw}")
        try {
            auth.currentUser!!.reauthenticate(credential).await()
            return true
        } catch (e: Exception) {
            Log.e("Exception_Authentication", ": $e")
            return false
        }
    }

    override suspend fun getCurrentUserUid(): String = auth.currentUser?.uid ?: ""

    override suspend fun getCurrentUserEmail(): String = auth.currentUser?.email ?: ""
}