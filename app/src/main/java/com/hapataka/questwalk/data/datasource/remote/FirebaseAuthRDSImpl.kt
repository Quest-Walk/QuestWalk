package com.hapataka.questwalk.data.datasource.remote

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hapataka.questwalk.domain.data.remote.AuthRDS
import javax.inject.Inject

class FirebaseAuthRDSImpl @Inject constructor() : AuthRDS {
    private val auth by lazy { Firebase.auth }

    override suspend fun registerByEmailAndPw(email: String, pw: String) {
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener {
                if (it.isSuccessful.not()) {
                    throw Exception(it.exception)
                }
            }
    }

    override suspend fun loginByEmailAndPw(email: String, pw: String) {
        auth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener {
                if(it.isSuccessful.not()) {
                    throw Exception(it.exception)
                }
            }
    }

    override suspend fun getCurrentUserInfo(): FirebaseUser? {
        return kotlin.runCatching { auth.currentUser }.getOrNull()
    }
}