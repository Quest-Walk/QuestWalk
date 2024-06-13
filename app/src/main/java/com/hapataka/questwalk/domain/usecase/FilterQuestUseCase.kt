package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.repository.backup.UserRepoImpl
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.util.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestFilteringUseCase {
    private val userRepoImpl = UserRepoImpl()
    private val questRepo = QuestStackRepositoryImpl()

    suspend operator fun invoke (): List<QuestStackEntity> = withContext(Dispatchers.IO) {
        val allQuests = questRepo.getAllItems()
        val successResults = userRepoImpl.getResultHistory(UserInfo.uid).filter { it.isSuccess }
        val successKeywords = successResults.map { it.quest }

        return@withContext allQuests.filterNot { successKeywords.contains(it.keyWord) }
    }
}