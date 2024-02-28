package com.hapataka.questwalk.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.History
import com.hapataka.questwalk.domain.repository.UserRepository

class UserRepositoryImpl: UserRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val userCollection by lazy { remoteDb.collection("user") }
    override fun setUserInfo(userId: String, result: History) {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(userId: String) {
        TODO("Not yet implemented")
    }

    override fun getAchieveHistory(userId: String) {
        TODO("Not yet implemented")
    }

    override fun getResultHistory(userId: String) {
        TODO("Not yet implemented")
    }

    override fun getUserHistory(userId: String) {
        TODO("Not yet implemented")
    }

}