package com.hapataka.questwalk.domain.data.local

import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.data.model.UserModel

interface CacheDataSource {
    fun setCurrentUser(user: UserModel)
    fun getCurrentUser(): UserModel?
    fun clearCurrentUserInfo()
    fun setCurrentUserHistories(histories: List<HistoryModel>)
    fun getCurrentUserHistories(): List<HistoryModel>?
}