package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.model.User
import javax.inject.Inject

class CheckAchievementUseCase @Inject constructor() {
    operator fun invoke(user: User): List<Int> {
        val successCount = user.successKeywords.size
        val results = mutableListOf<Int>()

        if (user.totalDistance > 42195f) {
            results += 2
        }

        if (successCount >= 1) {
            results += 0
        }

        if (successCount >= 3) {
            results += 1
        }

        return results
    }
}
