package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.core.model.AchieveItem
import com.hapataka.questwalk.core.remote.api.AchieveItemDataSource
import com.hapataka.questwalk.core.remote.model.AchieveItemDto
import javax.inject.Inject

class DefaultAchieveItemRepository @Inject constructor(
    private val achieveItemDataSource: AchieveItemDataSource,
) : AchieveItemRepository {

    override suspend fun getAchieveItems(): Result<List<AchieveItem>> {
        return runCatching {
            achieveItemDataSource.getAchieveItems().map { it.toModel() }
        }
    }

    private fun AchieveItemDto.toModel(): AchieveItem {
        return AchieveItem(
            achieveId = achieveId,
            achieveIcon = achieveIcon,
            achieveTitle = achieveTitle,
            achieveDescription = achieveDescription,
            isHidden = isHidden,
        )
    }
}
