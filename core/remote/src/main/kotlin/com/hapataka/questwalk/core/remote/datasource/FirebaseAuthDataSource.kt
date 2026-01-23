package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthDataSource {
    override suspend fun loginWithEmail(email: String, password: String): Result<String> {
        return kotlin.runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await().user!!.uid
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<String> {
        return kotlin.runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(credential).await().user!!.uid
        }
    }

    override suspend fun joinWithEmail(email: String, password: String): Result<String> {
        return kotlin.runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .await()
                .user!!.uid
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getUserId(): String {
        return firebaseAuth.uid ?: throw NoSuchElementException("no user id")
    }

    override suspend fun reauthenticate(password: String): Result<Unit> {
        return runCatching {
            val user = firebaseAuth.currentUser ?: throw Exception("User is null")
            val email = user.email ?: throw Exception("Email is null")
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return runCatching {
            firebaseAuth.currentUser?.delete()?.await()
                ?: throw Exception("User is null")
        }
    }

    override fun getUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }
}