package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.UserEntity

interface UserRepository {
    suspend fun setUserInfo(userId: String, profileId: Int, name: String)
    suspend fun updateUserInfo(userId: String, result: HistoryEntity)
    suspend fun getInfo(userId: String): UserEntity
    suspend fun getAllUserSize(): Long
    suspend fun getAchieveHistory(userId: String): MutableList<HistoryEntity.AchieveResultEntity>
    suspend fun getResultHistory(userId: String): MutableList<HistoryEntity.ResultEntity>
    suspend fun getUserHistory(userId: String): MutableList<HistoryEntity>
    suspend fun deleteUserData(userId: String)
}