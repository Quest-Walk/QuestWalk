package com.hapataka.questwalk.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetDustInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.GetWeatherInfoUseCase
import com.hapataka.questwalk.core.model.Dust
import com.hapataka.questwalk.core.model.Weather
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherInfoUseCase: GetWeatherInfoUseCase,
    private val getDustInfoUseCase: GetDustInfoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<WeatherUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadWeatherData()
    }

    fun onAction(action: WeatherAction) {
        when (action) {
            is WeatherAction.Refresh -> loadWeatherData()
        }
    }

    private fun loadWeatherData() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val weatherDeferred = async { getWeatherInfoUseCase() }
            val dustDeferred = async { getDustInfoUseCase() }

            val weatherResult = weatherDeferred.await()
            val dustResult = dustDeferred.await()

            if (weatherResult.isFailure || dustResult.isFailure) {
                _uiState.update {
                    UiState.Failure(
                        weatherResult.exceptionOrNull()
                            ?: dustResult.exceptionOrNull()
                            ?: Exception("Unknown error")
                    )
                }
                return@launch
            }

            val weatherList = weatherResult.getOrNull() ?: emptyList()
            val dust = dustResult.getOrNull() ?: Dust(-1, -1)

            _uiState.update {
                UiState.Success(
                    createUiState(weatherList, dust)
                )
            }
        }
    }

    private fun createUiState(weatherList: List<Weather>, dust: Dust): WeatherUiState {
        val requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH00"))
        val currentWeather = weatherList.firstOrNull { it.fcstTime == requestTime }

        val preview = WeatherPreviewUiModel(
            currentTemp = currentWeather?.temp ?: "0",
            skyState = getSkyState(currentWeather?.sky ?: "0"),
            precipState = getPrecipTypeState(currentWeather?.precipType ?: "0"),
            miseState = getMiseState(dust.pm10Value),
            choMiseState = getChoMiseState(dust.pm25Value),
        )

        return WeatherUiState(
            preview = preview,
            dust = DustUiModel(
                pm10Value = if (dust.pm10Value == -1) "통신 장애" else "${dust.pm10Value} ㎍/㎥",
                pm25Value = if (dust.pm25Value == -1) "통신 장애" else "${dust.pm25Value} ㎍/㎥",
            ),
            weatherItems = weatherList.map { weather ->
                WeatherItemUiModel(
                    time = weather.fcstTime,
                    temp = weather.temp,
                    sky = weather.sky,
                    precipType = weather.precipType,
                )
            }
        )
    }

    private fun getSkyState(sky: String): String {
        return when (sky.toIntOrNull() ?: 0) {
            in 0..5 -> "맑음 이구먼"
            in 6..8 -> "구름이 많구먼"
            else -> "많이 흐리겠구먼"
        }
    }

    private fun getPrecipTypeState(precipType: String): String {
        return when (precipType.toIntOrNull() ?: 0) {
            1, 4 -> "비가 올 수도 있겠어"
            2 -> "비 나 눈이 내릴 수도 있겠어"
            3 -> "눈이 올 수도 있겠어"
            else -> ""
        }
    }

    private fun getMiseState(pm10Value: Int): String {
        return when (pm10Value) {
            -1 -> "통신 장애로 인해 측정이 어렵고"
            in 0..30 -> "좋음 이고"
            in 31..80 -> "보통 이고"
            in 81..150 -> "나쁨 이고"
            else -> "매우 나쁨 이고"
        }
    }

    private fun getChoMiseState(pm25Value: Int): String {
        return when (pm25Value) {
            -1 -> "통신 장애로 인해 측정이 어렵구먼"
            in 0..15 -> "좋음 이구먼"
            in 16..35 -> "보통 이구먼"
            in 36..75 -> "나쁨 이구먼"
            else -> "매우 나쁨 이구먼"
        }
    }
}

data class WeatherUiState(
    val preview: WeatherPreviewUiModel = WeatherPreviewUiModel(),
    val dust: DustUiModel = DustUiModel(),
    val weatherItems: List<WeatherItemUiModel> = emptyList(),
)

data class WeatherPreviewUiModel(
    val currentTemp: String = "0",
    val skyState: String = "",
    val precipState: String = "",
    val miseState: String = "",
    val choMiseState: String = "",
)

data class DustUiModel(
    val pm10Value: String = "",
    val pm25Value: String = "",
)

data class WeatherItemUiModel(
    val time: String = "",
    val temp: String = "",
    val sky: String = "",
    val precipType: String = "",
)

sealed interface WeatherAction {
    data object Refresh : WeatherAction
}
