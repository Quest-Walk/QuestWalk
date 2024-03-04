package com.hapataka.questwalk.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    private val questStackRepositoryImpl = QuestStackRepositoryImpl()
    private var questKeyword: String? = null
    private var imgPath: String? = null

    var isPlay: Boolean = false

    suspend fun getQuestWithRepository() {
        val items =
            questStackRepositoryImpl.getAllItems()
        val size = items.size
        val idx = Random.nextInt(size)
        questKeyword = items[idx].keyWord
    }

    fun getKeyword() = questKeyword


    fun setImagePath(uri: String) {
        imgPath = uri
    }
}