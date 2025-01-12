package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.auth.FirebaseAuth
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
}