package com.hapataka.questwalk.ui.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.R
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.usecase.GetTmLocationUseCase
import com.hapataka.questwalk.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherViewModel (
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getTMLocationUseCase: GetTmLocationUseCase,
    private val dustRepo: DustRepository,
): ViewModel()  {
    private val _weatherInfo = MutableLiveData<MutableList<WeatherData>>()
    private val _dustInfo = MutableLiveData<DustEntity>()
    private val _weatherPreview = MutableLiveData<WeatherPreviewData>()
    private val _isLoading = MutableLiveData<Boolean>()
    val weatherInfo: LiveData<MutableList<WeatherData>> get() = _weatherInfo
    val dustInfo: LiveData<DustEntity> get() = _dustInfo
    val weatherPreview: LiveData<WeatherPreviewData> get() = _weatherPreview
    val isLoading: LiveData<Boolean> get() = _isLoading


    init {
        viewModelScope.launch {
            _isLoading.value = true
            val weatherDeferred = async { getWeatherInfo() }
            val dustDeferred = async { getDustInfo() }
            val list = mutableListOf(weatherDeferred, dustDeferred)

            list.awaitAll()
            setWeatherPreview()
        }
    }


    private suspend fun getWeatherInfo() {
        _weatherInfo.value = getWeatherUseCase().map {
            convertWeatherData(it)
        }.toMutableList()
    }
    private suspend fun getDustInfo() {
        val tmLocation = getTMLocationUseCase()
        val stationQueryMap = mapOf(
            "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
            "returnType" to "json",
            "tmX" to tmLocation.tmx,
            "tmY" to tmLocation.tmy
        )
        val stationName = dustRepo.getStation(stationQueryMap).stationName

        val dustQueryMap = mapOf(
            "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
            "returnType" to "json",
            "stationName" to stationName,
            "dataTerm" to "DAILY",
            "ver" to "1.0"
        )
        _dustInfo.value = dustRepo.getDustInfo(dustQueryMap)
    }

    private fun setWeatherPreview() {
        val requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH00"))
        val currentWeather = _weatherInfo.value?.first { it.fcstTime == requestTime }
        _weatherPreview.value = WeatherPreviewData(
            currentTmp = currentWeather?.temp ?: "0",
            sky = getSkyState(currentWeather?.sky ?: "0"),
            precipType = getPrecipTypeState(currentWeather?.precipType ?: "0"),
            miseState = getMiseState(_dustInfo.value?.pm10Value ?: 0),
            choMiseState = getChoMiseState(_dustInfo.value?.pm25Value ?: 0)
        )
        _isLoading.value = false
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

    private fun getSkyState(sky: String): String {
        return when(sky.toInt()) {
            in 0..5 -> "맑음 이구먼"
            in 6..8 -> "구름이 많구먼"
            else -> "많이 흐리겠구먼"
        }
    }

    private fun getPrecipTypeState(precipType: String): String {
        return when(precipType.toInt()) {
            1,4 -> "비가 올 수도 있겠어"
            2 -> "비 나 눈이 내릴 수도 있겠어"
            3 -> "눈이 올 수도 있겠어"
            else -> ""
        }
    }

    private fun getMiseState(pm10Value: Int): String {
        return when(pm10Value) {
            in 0..30 -> "좋음 이고"
            in 31..80 -> "보통 이고"
            in 81..150 -> "나쁨 이고"
            else -> "매우 나쁨 이고"
        }
    }

    private fun getChoMiseState(pm25Value: Int): String {
        return when(pm25Value) {
            in 0..15 -> "좋음 이구먼"
            in 16..35 -> "보통 이구먼"
            in 36..75 -> "나쁨 이구먼"
            else -> "매우 나쁨 이구먼"
        }
    }
}