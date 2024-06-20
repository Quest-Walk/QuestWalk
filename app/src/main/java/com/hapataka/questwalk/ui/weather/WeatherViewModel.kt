package com.hapataka.questwalk.ui.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.domain.usecase.GetWeatherStateUseCase
import com.hapataka.questwalk.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: GetWeatherUseCase,
    private val weatherStateUseCase: GetWeatherStateUseCase
) : ViewModel() {
    private val _weatherModel = MutableLiveData<WeatherModel>()
    val weatherModel: LiveData<WeatherModel> get() = _weatherModel

    private val _weatherState = MutableLiveData<Int>()
    val weatherState: LiveData<Int> get() = _weatherState

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getWeatherModel() {
        viewModelScope.launch {
            val weatherModel = weatherUseCase()

            weatherModel.fold(
                onSuccess = {
                    _weatherModel.value = it
                    _error.value = false
                    getWeatherState(it)
                },
                onFailure = {
                    Log.d("WeatherViewModel", "getWeatherModel: ${it.message}")
                    _error.value = true
                }
            )
        }
    }

        private fun getWeatherState(weatherModel: WeatherModel?) {
            viewModelScope.launch {
                weatherModel?.let {
                    _weatherState.value = weatherStateUseCase(weatherModel)
                }
            }
        }
    }