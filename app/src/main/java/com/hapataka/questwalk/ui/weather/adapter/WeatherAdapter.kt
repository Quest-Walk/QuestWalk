package com.hapataka.questwalk.ui.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.ItemWeatherBinding
import com.hapataka.questwalk.ui.weather.WeatherData

class WeatherAdapter: ListAdapter<WeatherData, WeatherAdapter.WeatherViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherViewHolder(ItemWeatherBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WeatherViewHolder(private val binding: ItemWeatherBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeatherData) {
            with(binding) {
                tvTime.text = item.fcstTime
                tvTmp.text = "${item.temp}°"
                // TODO: 하늘상태 / 강수량을 통한 이미지 설정 로직 추가
                ivWeather.load(R.drawable.ic_weather)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WeatherData>() {
            override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
                return oldItem.fcstDate == newItem.fcstDate && oldItem.fcstTime == newItem.fcstTime
            }

            override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
                return oldItem == newItem
            }
        }
    }
}