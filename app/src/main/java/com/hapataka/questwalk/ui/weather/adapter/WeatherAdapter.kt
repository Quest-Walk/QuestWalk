package com.hapataka.questwalk.ui.weather.adapter

import android.graphics.drawable.Drawable
import android.util.Log
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
                tvTime.text = item.fcstTime.substring(0,2) + "시"
                tvTmp.text = "${item.temp}도"
                Log.d("WeatherAdapter","PTY:${item.precipType}, SKY:${item.sky}")
                setPtyState(item.precipType.toInt(), item.sky.toInt())
            }
        }

        private fun setPtyState(pty: Int, sky: Int) {
            val weatherIcon = when(pty) {
                1,4 -> R.drawable.ic_pty1
                2 -> R.drawable.ic_pty2
                3 -> R.drawable.ic_pty3
                else -> 0
            }
            binding.ivWeather.load(if (weatherIcon == 0) setSkyState(sky) else weatherIcon )
        }

        private fun setSkyState(sky: Int): Int {
            return when(sky) {
                in 0..5 -> R.drawable.ic_sky1
                in 6..8 -> R.drawable.ic_sky2
                else -> R.drawable.ic_sky3
            }
        }
    }



//     PTY 강수 형태 (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
//             (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
// SKY 하늘 상태 0~5 맑음 6~8 구름 많음 9~10 흐림
// T1H 기온

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