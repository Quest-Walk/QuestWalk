package com.hapataka.questwalk.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: GetWeatherUseCase,
): ViewModel() {
    private val _weatherModel = MutableLiveData<WeatherModel>()
    val weatherModel: LiveData<WeatherModel> get() = _weatherModel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        viewModelScope.launch {
            _weatherModel.value = weatherUseCase()
        }
    }

}