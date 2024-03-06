package com.hapataka.questwalk.ui.home

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.domain.usecase.QuestFilteringUseCase
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    private val questRepo = QuestStackRepositoryImpl()
    private var _currentKeyword = MutableLiveData<String> ()
    val currentKeyword: LiveData<String> get() = _currentKeyword

    private val filteringUseCase = QuestFilteringUseCase()

    private var imgPath: Uri? = null

    var isPlay: Boolean = false
    var isQuestSuccess : Boolean = false
    var questKeyword = ""

    fun getRandomKeyword() {
        viewModelScope.launch {
            val remainingKeyword = filteringUseCase().map { it.keyWord }

            _currentKeyword.value = remainingKeyword.random()
        }
    }

    suspend fun getQuestWithRepository() {
        val items =
            questRepo.getAllItems()
        val size = items.size
        val idx = Random.nextInt(size)
        questKeyword = items[idx].keyWord
    }

    fun getKeyword() = questKeyword

    fun setKeyword(keyword: String) {
        questKeyword = keyword
    }


    fun setImagePath(uri: String) {
        imgPath = uri.toUri()
    }
}