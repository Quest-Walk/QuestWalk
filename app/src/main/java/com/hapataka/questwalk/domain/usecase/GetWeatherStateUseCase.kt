package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherStateUseCase @Inject constructor() {
    operator fun invoke(weatherModel: WeatherModel): Int {
        val currentForecastModel = weatherModel.forecastList.first()
        val temperature = currentForecastModel.temp.toInt()
        val skyCondition = currentForecastModel.sky.toInt()
        val precipitation = currentForecastModel.precipType.toInt()
        var totalRating = 1 // 0점 ~ 20점 정수를 평가

        if (precipitation in 1..4) {
            return 4
        }

        totalRating += when(weatherModel.pm10Grade) {
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            else -> 0
        }

        totalRating += when(weatherModel.pm25Grade) {
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            else -> 0
        }

        totalRating += when {
            (temperature < -5 || temperature > 35) -> 4
            (temperature < 0 || temperature > 30) -> 3
            (temperature < 5 || temperature > 25) -> 2
            (temperature < 10 || temperature > 20) -> 1
            else -> 0
        }

        totalRating += when (skyCondition) {
            in 0..5 -> 1
            in 6..8 -> 2
            in 9..10 -> 3
            else -> 0
        }

        return when(totalRating) {
            in 4..7 -> 1 // 외출하기 좋은 날씨
            in 8..10 -> 2 // 외출하기 무난한 날씨
            in 11..13 -> 3 // 외출하기 약간 안 좋은 날씨
            in 14..16 -> 4 // 외출하기 나쁜 날씨
            else -> throw IllegalArgumentException("총점이 유효하지 않습니다.")
        }
    }
}