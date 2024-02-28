package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.Achievement
import com.hapataka.questwalk.domain.entity.AchievementEntity
import com.hapataka.questwalk.domain.entity.History
import com.hapataka.questwalk.domain.entity.ResultEntity
import com.hapataka.questwalk.domain.entity.UserEntity

interface UserRepository {
    fun setUserInfo(userId: String, result: History)
    fun getUserInfo(userId: String)
    fun getAchieveHistory(userId: String)
    fun getResultHistory(userId: String)
    fun getUserHistory(userId: String)
}