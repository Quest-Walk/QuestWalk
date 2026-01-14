package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.WeatherRepository
import com.hapataka.questwalk.core.model.Dust
import javax.inject.Inject

class GetDustInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    suspend operator fun invoke(): Result<Dust> {
        return weatherRepository.getDustInfo()
    }
}
