package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestFilteringUseCase {
    private val authRepo = AuthRepositoryImpl()
    private val userRepo = UserRepositoryImpl()
    private val questRepo = QuestStackRepositoryImpl()

    suspend operator fun invoke (): List<QuestStackEntity> = withContext(Dispatchers.IO) {
        val allQuests = questRepo.getAllItems()
        val successResults = userRepo.getResultHistory(authRepo.getCurrentUserUid()).filter { it.isFailed.not() }
        val successKeywords = successResults.map { it.quest }

        return@withContext allQuests.filterNot { successKeywords.contains(it.keyWord) }
    }
}