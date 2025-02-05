package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.core.model.History

interface MapRepository {
    fun drawPath(result: History.QuestResult)
}