package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.WeatherRepository
import com.hapataka.questwalk.core.model.Weather
import javax.inject.Inject

class GetWeatherInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(): Result<List<Weather>> {
        return weatherRepository.getWeatherInfo()
    }
}
