package com.hapataka.questwalk.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.WeatherRepository
import com.hapataka.questwalk.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
): ViewModel()  {
    private val _weatherInfo = MutableLiveData<MutableList<WeatherData>>()
    val weatherInfo: LiveData<MutableList<WeatherData>> =  _weatherInfo

    fun getWeatherInfo() {
        viewModelScope.launch{
            _weatherInfo.value = getWeatherUseCase().map {
                convertWeatherData(it)
            }.toMutableList()
        }
    }

    private fun convertWeatherData(weatherEntity: WeatherEntity): WeatherData {
        return WeatherData(
            fcstDate = weatherEntity.fcstDate,
            fcstTime = weatherEntity.fcstTime,
            sky = weatherEntity.sky,
            precipType = weatherEntity.precipType,
            temp = weatherEntity.temp
        )
    }
}