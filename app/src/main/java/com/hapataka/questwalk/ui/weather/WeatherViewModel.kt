package com.hapataka.questwalk.ui.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.usecase.GetTmLocationUseCase
import com.hapataka.questwalk.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.launch

class WeatherViewModel (
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getTMLocationUseCase: GetTmLocationUseCase,
    private val dustRepo: DustRepository,
): ViewModel()  {
    private val _weatherInfo = MutableLiveData<MutableList<WeatherData>>()
    private val _dustInfo = MutableLiveData<DustEntity>()
    val weatherInfo: LiveData<MutableList<WeatherData>> get() = _weatherInfo
    val dustInfo: LiveData<DustEntity> get() = _dustInfo


    init {
        getWeatherInfo()
        getStation()
    }

    private fun getWeatherInfo() {
        viewModelScope.launch{
            _weatherInfo.value = getWeatherUseCase().map {
                convertWeatherData(it)
            }.toMutableList()
        }
    }

    private fun getDustInfo(station: String) {
        viewModelScope.launch {
            val map = mapOf(
                "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
                "returnType" to "json",
                "stationName" to station,
                "dataTerm" to "DAILY",
                "ver" to "1.0"
            )
            _dustInfo.value = dustRepo.getDustInfo(map)
        }
    }

    private fun getStation() {
        viewModelScope.launch {
            val tmLocation = getTMLocationUseCase()
            val map = mapOf(
                "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
                "returnType" to "json",
                "tmX" to tmLocation.tmx,
                "tmY" to tmLocation.tmy
            )
            getDustInfo(dustRepo.getStation(map).stationName)
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