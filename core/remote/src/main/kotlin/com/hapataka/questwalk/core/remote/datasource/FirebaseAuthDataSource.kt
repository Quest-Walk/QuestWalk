package com.hapataka.questwalk.core.remote.datasource

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
                .also { firebaseAuth.signOut() }
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getUserId(): String {
        return firebaseAuth.uid ?: throw NoSuchElementException("no user id")
    }
}