package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.HistoryEntity

interface MapRepository {
    fun drawPath(result: HistoryEntity.ResultEntity)
}