package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.dataapi.datasource.QuestRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.QuestDto
import com.hapataka.questwalk.core.dataapi.model.SuccessItemDto
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultQuestRepository @Inject constructor(
    private val questRemoteDataSource: QuestRemoteDataSource,
) : QuestRepository {

    private val _currentQuest = MutableStateFlow<Quest?>(null)
    override val currentQuest: StateFlow<Quest?> = _currentQuest.asStateFlow()

    override fun setCurrentQuest(quest: Quest) {
        _currentQuest.value = quest
    }

    override fun clearCurrentQuest() {
        _currentQuest.value = null
    }

    override suspend fun getAllQuests(): Result<List<Quest>> {
        return questRemoteDataSource.getAllQuests().map { dtos ->
            dtos.map { it.toModel() }
        }
    }

    override suspend fun getQuestByKeyword(keyword: String): Result<Quest> {
        return questRemoteDataSource.getQuestByKeyword(keyword).map { dto ->
            dto?.toModel() ?: throw NoSuchElementException("Quest not found: $keyword")
        }
    }

    override suspend fun updateQuestSuccess(
        keyword: String,
        userId: String,
        imageUrl: String,
        registerAt: String,
    ): Result<Unit> {
        val successItem = SuccessItemDto(
            userId = userId,
            imageUrl = imageUrl,
            registerAt = registerAt,
        )
        return questRemoteDataSource.updateQuestSuccess(keyword, successItem)
    }

    private fun QuestDto.toModel(): Quest = Quest(
        keyword = keyword,
        level = level,
        successItems = successItems.map { it.toModel() },
    )

    private fun SuccessItemDto.toModel(): Quest.SuccessItem = Quest.SuccessItem(
        userId = userId,
        imageUrl = imageUrl,
        registerAt = registerAt,
    )
}
