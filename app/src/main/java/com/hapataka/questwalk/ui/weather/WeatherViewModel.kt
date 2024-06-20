package com.hapataka.questwalk.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.domain.usecase.GetWeatherStateUseCase
import com.hapataka.questwalk.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: GetWeatherUseCase,
    private val weatherStateUseCase: GetWeatherStateUseCase
) : ViewModel() {
    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading(true))
    val weatherUiState: StateFlow<WeatherUiState> get() = _weatherUiState

    init {
        viewModelScope.launch {
            _weatherUiState.value = weatherUseCase().fold(
                onSuccess = {
                    val weatherLevel = weatherStateUseCase(it)
                    WeatherUiState.Success(it, weatherLevel)
                },
                onFailure = {
                    WeatherUiState.Error(true)
                }
            )
            _weatherUiState.value = WeatherUiState.Loading(false)
        }
    }

}