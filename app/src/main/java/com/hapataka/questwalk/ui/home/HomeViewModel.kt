package com.hapataka.questwalk.ui.home

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    private val questStackRepositoryImpl = QuestStackRepositoryImpl()
    private var questKeyword: String? = null
    private var imgPath: Uri? = null

    var isPlay: Boolean = false
    var isQuestSuccess : Boolean = false

    suspend fun getQuestWithRepository() {
        val items =
            questStackRepositoryImpl.getAllItems()
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