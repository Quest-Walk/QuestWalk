package com.hapataka.questwalk.data.firebase.repository

import com.hapataka.questwalk.domain.entity.History
import com.hapataka.questwalk.domain.repository.UserRepository

class UserRepositoryImpl: UserRepository {
    override fun signUp(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun signIn(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun updateUserHistory(userId: String, history: History) {
        TODO("Not yet implemented")
    }

    override fun loadAllHistory() {
        TODO("Not yet implemented")
    }

    override fun loadUserAllAchievements() {
        TODO("Not yet implemented")
    }

    override fun loadUserAchievement(achievementId: Int) {
        TODO("Not yet implemented")
    }

    override fun loadUserResult(keyword: String) {
        TODO("Not yet implemented")
    }
}