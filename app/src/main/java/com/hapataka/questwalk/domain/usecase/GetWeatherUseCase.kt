package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<WeatherModel> {
        val currentLocation = locationRepository.getCurrent().location

        return weatherRepository.getWeatherInfo(currentLocation)
    }
}