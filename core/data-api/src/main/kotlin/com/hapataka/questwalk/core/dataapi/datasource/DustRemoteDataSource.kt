package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.DustDto
import com.hapataka.questwalk.core.dataapi.model.LocationDto

interface DustRemoteDataSource {
    suspend fun getDust(location: LocationDto): Result<DustDto>
}
