package com.hapataka.questwalk.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.AchieveItemEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.record.model.RecordItem.ResultItem
import kotlinx.coroutines.launch

class RecordViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val achieveItemRepo: AchieveItemRepository
) : ViewModel() {
    private var _recordItems = MutableLiveData<List<RecordItem>>()
    val recordItems: LiveData<List<RecordItem>> get() = _recordItems

    private var _achieveItems = MutableLiveData<List<AchieveItem>> ()
    val achieveItems: LiveData<List<AchieveItem>> get() = _achieveItems


    fun getRecordItems() {
        viewModelScope.launch {
            var currentItems = mutableListOf<RecordItem>()
            val histories =userRepo.getUserHistory(authRepo.getCurrentUserUid())
            val achieveItems = achieveItemRepo.getAchieveItem().map {entity ->
                convertToRecordItem(entity, histories.filterIsInstance<AchieveResultEntity>())
            }

            _achieveItems.value = achieveItems
            histories.forEach { entity ->
                when (entity) {
                    is ResultEntity -> {
                        currentItems += ResultItem(
                            entity.quest,
                            entity.questImg,
                            entity.isSuccess,
                            entity.registerAt
                        )
                    }

                    is AchieveResultEntity -> {
                        currentItems += achieveItems.filter { it.achieveId == entity.achievementId }
                    }
                }
            }
            _recordItems.value = currentItems
        }
    }

    private fun convertToRecordItem(entity: AchieveItemEntity, history: List<AchieveResultEntity>): AchieveItem {
        return with(entity) {
            AchieveItem(
                achieveId,
                achieveIcon,
                achieveTitle,
                achieveDescription,
                history.any { it.achievementId == entity.achieveId }
            )
        }
    }
}