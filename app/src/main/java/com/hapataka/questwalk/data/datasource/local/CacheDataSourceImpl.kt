package com.hapataka.questwalk.data.datasource.local

import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import javax.inject.Inject

class CacheDataSourceImpl @Inject constructor() : CacheDataSource {
    private var currentUser: UserModel? = null
    private var currentUserHistories: List<HistoryModel>? = null
    override fun setCurrentUser(user: UserModel) {
        this.currentUser = user
    }

    override fun setCurrentUserHistories(
        histories: List<HistoryModel>
    ) {
        currentUserHistories = histories.sortedBy { it.registerAt }
    }

    override fun getCurrentUser(): UserModel? {
        return currentUser
    }

    override fun getCurrentUserHistories(): List<HistoryModel>? {
        return currentUserHistories
    }

    override fun clearCurrentUserInfo() {
        currentUser = null
        currentUserHistories = null
    }
}