package com.hapataka.questwalk.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.hapataka.questwalk.data.model.UserModel

interface UserRepository {
    suspend fun getCurrentUser(): UserModel?
    suspend fun cacheUser(user: UserModel)
    suspend fun uploadUser(user: UserModel)
}