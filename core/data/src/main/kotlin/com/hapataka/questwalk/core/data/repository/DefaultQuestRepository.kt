package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import com.hapataka.questwalk.core.remote.api.QuestDataSource
import com.hapataka.questwalk.core.remote.model.QuestDto
import javax.inject.Inject

class DefaultQuestRepository @Inject constructor(
    private val questDataSource: QuestDataSource,
) : QuestRepository {

    override suspend fun getAllQuests(): Result<List<Quest>> {
        return runCatching {
            questDataSource.getAllQuests().map { it.toModel() }
        }
    }

    override suspend fun getQuestByKeyword(keyword: String): Result<Quest> {
        return runCatching {
            questDataSource.getQuestByKeyword(keyword)?.toModel()
                ?: throw NoSuchElementException("Quest not found: $keyword")
        }
    }

    override suspend fun updateQuestSuccess(
        keyword: String,
        userId: String,
        imageUrl: String,
        registerAt: String,
    ): Result<Unit> {
        return runCatching {
            questDataSource.updateQuestSuccess(keyword, userId, imageUrl, registerAt)
        }
    }

    private fun QuestDto.toModel(): Quest {
        return Quest(
            keyword = keyWord,
            level = level,
            successItems = successItems.map { it.toModel() }
        )
    }

    private fun QuestDto.SuccessItemDto.toModel(): Quest.SuccessItem {
        return Quest.SuccessItem(
            userId = userId,
            imageUrl = imageUrl,
            registerAt = registerAt
        )
    }
}
