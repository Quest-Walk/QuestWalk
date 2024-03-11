package com.hapataka.questwalk.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.data.resource.Achievements
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchievementEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.record.model.RecordItem.ResultItem
import kotlinx.coroutines.launch

class RecordViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl
) : ViewModel() {
    private var _recordItems = MutableLiveData<List<RecordItem>>()
    val recordItems: LiveData<List<RecordItem>> get() = _recordItems
    private val achieveResource = Achievements.list.toList()

    fun getRecordItems() {
        viewModelScope.launch {
            val histories = userRepo.getUserHistory(authRepo.getCurrentUserUid())
            var currentItems = mutableListOf<RecordItem>()

            histories.forEach { entity ->
                when (entity) {
                    is ResultEntity -> {
                        currentItems += ResultItem(entity.quest, entity.questImg, entity.isFailed, entity.registerAt)
                    }

                    is AchievementEntity -> {
                        currentItems += achieveResource.filterIsInstance<AchieveItem>()
                            .filter { it.achieveId == entity.achievementId }
                    }

                    else -> throw IllegalArgumentException("unknown entity")
                }
            }
            _recordItems.value = currentItems
        }
    }
}