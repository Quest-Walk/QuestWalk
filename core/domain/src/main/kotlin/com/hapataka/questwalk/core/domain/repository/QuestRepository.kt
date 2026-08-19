package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Quest
import kotlinx.coroutines.flow.StateFlow

interface QuestRepository {
    val currentQuest: StateFlow<Quest?>

    suspend fun getAllQuests(): Result<List<Quest>>
    suspend fun getQuestByKeyword(keyword: String): Result<Quest>
    suspend fun updateQuestSuccess(keyword: String, userId: String, imageUrl: String, registerAt: String): Result<Unit>

    fun setCurrentQuest(quest: Quest)
    fun clearCurrentQuest()

    suspend fun verifyPhotoKeyword(filePath: String, keyword: String): Result<KeywordMatchResult>
    suspend fun uploadQuestImage(filePath: String, userId: String, keyword: String): Result<ImageUploadResult>
    fun startBackgroundUpload(filePath: String, userId: String, keyword: String)
    suspend fun awaitUploadResult(): Result<ImageUploadResult>
    suspend fun deletePhotoFile(filePath: String)
}

data class KeywordMatchResult(
    val isSuccess: Boolean,
    val recognizedText: String,
    val matchedText: String,
    val similarity: Double,
)

data class ImageUploadResult(
    val remoteUrl: String,
    val localPath: String,
)
