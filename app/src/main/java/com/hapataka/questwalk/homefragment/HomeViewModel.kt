package com.hapataka.questwalk.homefragment

import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    private val questStackRepositoryImpl = QuestStackRepositoryImpl()
    private var imgPath: String = ""
    suspend fun getQuestWithRepository(): String {
        val items =
            questStackRepositoryImpl.getAllItems()
        val size = items.size
        val idx = Random.nextInt(size)
        return items[idx].keyWord
    }

    fun setImagePath(uri: String){
        imgPath = uri
    }
}