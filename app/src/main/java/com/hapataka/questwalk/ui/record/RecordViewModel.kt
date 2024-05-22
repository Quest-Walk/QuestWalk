package com.hapataka.questwalk.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.AchieveItemEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.domain.repository.UserRDS
import com.hapataka.questwalk.ui.fragment.record.model.RecordItem
import com.hapataka.questwalk.ui.fragment.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.fragment.record.model.RecordItem.ResultItem
import com.hapataka.questwalk.util.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val userRepo: UserRDS,
    private val achieveItemRepo: AchieveItemRepository
) : ViewModel() {
    private var _recordItems = MutableLiveData<List<RecordItem>>()
    val recordItems: LiveData<List<RecordItem>> get() = _recordItems

    private var _achieveItems = MutableLiveData<List<AchieveItem>> ()
    val achieveItems: LiveData<List<AchieveItem>> get() = _achieveItems

    fun getRecordItems() {
        viewModelScope.launch {
            var currentItems = mutableListOf<RecordItem>()
            val histories =userRepo.getUserHistory(UserInfo.uid)
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