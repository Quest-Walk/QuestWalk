package com.hapataka.questwalk.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetWeatherInfoUseCase
import com.hapataka.questwalk.core.model.Forecast
import com.hapataka.questwalk.core.model.PrecipType
import com.hapataka.questwalk.core.model.SkyType
import com.hapataka.questwalk.core.model.Weather
import com.hapataka.questwalk.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherInfoUseCase: GetWeatherInfoUseCase,
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

            getWeatherInfoUseCase()
                .onSuccess { weather ->
                    _uiState.update {
                        UiState.Success(createUiState(weather))
                    }
                }
                .onFailure { e ->
                    _uiState.update { UiState.Failure(e) }
                }
        }
    }

    private fun createUiState(weather: Weather): WeatherUiState {
        val current = weather.current
        val isWeatherAvailable = current.temp != WEATHER_UNAVAILABLE

        val preview = WeatherPreviewUiModel(
            currentTemp = if (isWeatherAvailable) "${current.temp}" else "-",
            skyState = if (isWeatherAvailable) getSkyState(current.sky) else "날씨 정보를 가져올 수 없구먼",
            precipState = if (isWeatherAvailable) getPrecipTypeState(current.precipType) else "",
            miseState = getMiseState(weather.pm10),
            choMiseState = getChoMiseState(weather.pm25),
        )

        val forecastItems = weather.forecasts.map { forecast ->
            WeatherItemUiModel(
                time = forecast.fcstTime,
                temp = if (forecast.temp != WEATHER_UNAVAILABLE) "${forecast.temp}" else "-",
                sky = if (forecast.temp != WEATHER_UNAVAILABLE) forecast.sky.toDisplayString() else "통신 장애",
                precipType = if (forecast.temp != WEATHER_UNAVAILABLE) forecast.precipType.toDisplayString() else "-",
            )
        }

        return WeatherUiState(
            preview = preview,
            dust = DustUiModel(
                pm10Value = if (weather.pm10 == -1) "통신 장애" else "${weather.pm10} ㎍/㎥",
                pm25Value = if (weather.pm25 == -1) "통신 장애" else "${weather.pm25} ㎍/㎥",
            ),
            region = weather.region.name,
            forecasts = forecastItems,
        )
    }

    private fun getSkyState(sky: SkyType): String {
        return when (sky) {
            SkyType.CLEAR -> "맑음 이구먼"
            SkyType.CLOUDY -> "구름이 많구먼"
            SkyType.OVERCAST -> "많이 흐리겠구먼"
        }
    }

    private fun getPrecipTypeState(precipType: PrecipType): String {
        return when (precipType) {
            PrecipType.NONE -> ""
            PrecipType.RAIN, PrecipType.SHOWER -> "비가 올 수도 있겠어"
            PrecipType.RAIN_SNOW -> "비나 눈이 내릴 수도 있겠어"
            PrecipType.SNOW -> "눈이 올 수도 있겠어"
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

    private fun SkyType.toDisplayString(): String {
        return when (this) {
            SkyType.CLEAR -> "맑음"
            SkyType.CLOUDY -> "구름많음"
            SkyType.OVERCAST -> "흐림"
        }
    }

    private fun PrecipType.toDisplayString(): String {
        return when (this) {
            PrecipType.NONE -> "없음"
            PrecipType.RAIN -> "비"
            PrecipType.SNOW -> "눈"
            PrecipType.RAIN_SNOW -> "비/눈"
            PrecipType.SHOWER -> "소나기"
        }
    }

    companion object {
        private const val WEATHER_UNAVAILABLE = -99
    }
}

data class WeatherUiState(
    val preview: WeatherPreviewUiModel = WeatherPreviewUiModel(),
    val dust: DustUiModel = DustUiModel(),
    val region: String = "",
    val forecasts: List<WeatherItemUiModel> = emptyList(),
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
