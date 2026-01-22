package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.model.LocationUpdate
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): Location?

    /**
     * 실시간 위치 업데이트 Flow
     * 속도와 정확도 기반 필터링이 적용됨
     */
    fun getLocationUpdates(): Flow<LocationUpdate>
}
