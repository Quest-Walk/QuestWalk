package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AchieveItemRepository
import com.hapataka.questwalk.core.model.AchieveItem
import javax.inject.Inject

class GetAchieveItemsUseCase @Inject constructor(
    private val achieveItemRepository: AchieveItemRepository,
) {
    suspend operator fun invoke(): Result<List<AchieveItem>> {
        return achieveItemRepository.getAchieveItems()
    }
}
