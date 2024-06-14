package com.hapataka.questwalk.data.datasource.remote

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hapataka.questwalk.domain.data.remote.AuthRDS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRDS @Inject constructor() : AuthRDS {
    private val auth by lazy { Firebase.auth }

    override suspend fun registerByEmailAndPw(email: String, pw: String): Result<Boolean> {
        return kotlin.runCatching {
            auth.createUserWithEmailAndPassword(email, pw).await().user != null
        }
    }

    override suspend fun loginByEmailAndPw(email: String, pw: String): Result<Boolean> {
        return kotlin.runCatching {
            auth.signInWithEmailAndPassword(email, pw).await().user != null
        }
    }

    override suspend fun getCurrentUserInfo(): FirebaseUser? {
        return kotlin.runCatching { auth.currentUser }.getOrNull()
    }

    override suspend fun logout(): Result<Unit> {
        return kotlin.runCatching { auth.signOut() }
    }

    override suspend fun reauthCurrentUser(pw: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User is null"))
        val email = user.email ?: return Result.failure(Exception("Email is null"))
        val credential = EmailAuthProvider.getCredential(email, pw)

        return kotlin.runCatching {
            user.reauthenticate(credential).await()
        }
    }

    override suspend fun dropOutCurrentUser(): Result<Unit> {
        return kotlin.runCatching { auth.currentUser?.delete() }
    }


}