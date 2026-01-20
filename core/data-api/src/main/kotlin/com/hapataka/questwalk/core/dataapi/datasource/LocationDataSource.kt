package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.LocationDto

interface LocationDataSource {
    suspend fun getCurrentLocation(): LocationDto?
}
