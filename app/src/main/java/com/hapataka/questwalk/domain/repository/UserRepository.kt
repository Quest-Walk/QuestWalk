package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.Achievement
import com.hapataka.questwalk.domain.entity.AchievementEntity
import com.hapataka.questwalk.domain.entity.History
import com.hapataka.questwalk.domain.entity.ResultEntity
import com.hapataka.questwalk.domain.entity.UserEntity

interface UserRepository {
    fun signUp(email:String, password:String)
    fun signIn(email:String, password:String)


    fun updateUserHistory(userId: String, history: History)
    fun loadAllHistory()
    fun loadUserAllAchievements()

    fun loadUserAchievement(achievementId: Int)
    fun loadUserResult(keyword: String)

//    fun updateUserAchievement(achievement: AchievementEntity)
//    fun updateUserResult(result: ResultEntity)
//
//    fun loadUserAllAchievements()
//    fun loadUserAllResults()
//
//    fun loadUserAchievement(achievementId: Int)
//    fun loadUserResult(keyword: String)

}