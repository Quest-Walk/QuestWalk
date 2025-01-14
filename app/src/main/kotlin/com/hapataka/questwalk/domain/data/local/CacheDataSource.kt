package com.hapataka.questwalk.domain.data.local

import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.data.model.UserModel

interface CacheDataSource {
    fun setCurrentUser(user: UserModel)
    fun getCurrentUser(): UserModel?
    fun clearCurrentUserInfo()
    fun setCurrentUserHistories(histories: List<History>)
    fun getCurrentUserHistories(): List<History>?
}