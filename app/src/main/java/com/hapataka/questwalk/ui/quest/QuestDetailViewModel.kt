package com.hapataka.questwalk.ui.quest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import kotlinx.coroutines.launch

//class QuestDetailViewModel: ViewModel() {
//    private val userRepositoryImpl = UserRepositoryImpl()
//    private val _questItem = MutableLiveData<HistoryEntity.ResultEntity>()
//    val questItem: LiveData<HistoryEntity.ResultEntity> = _questItem
//
//    fun getQuestItem(userId: String, imageUrl: String) {
//        viewModelScope.launch {
//            val userResults = userRepositoryImpl.getResultHistory(userId)
//            _questItem.value = userResults.first {
//                it.questImg == imageUrl
//            }
//        }
//    }
//}