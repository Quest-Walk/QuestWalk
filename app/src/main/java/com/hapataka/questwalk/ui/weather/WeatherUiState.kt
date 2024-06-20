package com.hapataka.questwalk.ui.weather

import com.hapataka.questwalk.data.model.WeatherModel

sealed class WeatherUiState {
    data class Loading(val loading: Boolean): WeatherUiState()
    data class Success(val data: WeatherModel, val level: Int): WeatherUiState()
    data class Error(val error: Boolean): WeatherUiState()
}