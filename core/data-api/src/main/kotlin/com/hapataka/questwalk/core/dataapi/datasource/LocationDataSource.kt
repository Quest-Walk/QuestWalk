package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.LocationDto
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    suspend fun getCurrentLocation(): LocationDto?

    /**
     * 실시간 위치 업데이트 Flow
     * @param intervalMs 위치 업데이트 간격 (밀리초)
     */
    fun getLocationUpdates(intervalMs: Long = 2000L): Flow<LocationDto>
}
