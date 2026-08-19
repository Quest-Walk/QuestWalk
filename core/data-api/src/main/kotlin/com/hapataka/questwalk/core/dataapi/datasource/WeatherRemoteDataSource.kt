package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.LocationDto
import com.hapataka.questwalk.core.dataapi.model.WeatherDto

interface WeatherRemoteDataSource {
    suspend fun getWeather(location: LocationDto): Result<WeatherDto>
}
