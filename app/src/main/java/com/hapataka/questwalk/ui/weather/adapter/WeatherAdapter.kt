package com.hapataka.questwalk.ui.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.databinding.ItemWeatherBinding

class WeatherAdapter(val context: Context): ListAdapter<WeatherModel.ForecastModel, WeatherAdapter.WeatherViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeatherViewHolder(ItemWeatherBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WeatherViewHolder(private val binding: ItemWeatherBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeatherModel.ForecastModel) {
            with(binding) {
                if (item.fcstTime == "0000") {
                    tvTime.text = "내일"
                    tvTime.setTextColor(context.getColor(R.color.main_purple))
                } else {
                    tvTime.text = item.fcstTime.substring(0,2) + "시"
                    tvTime.setTextColor(context.getColor(R.color.black))
                }

                tvTmp.text = "${item.temp}도"
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
                1 -> R.drawable.ic_sky1
                3 -> R.drawable.ic_sky2
                else -> R.drawable.ic_sky3
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WeatherModel.ForecastModel>() {
            override fun areItemsTheSame(oldItem: WeatherModel.ForecastModel, newItem: WeatherModel.ForecastModel): Boolean {
                return oldItem.fcstDate == newItem.fcstDate && oldItem.fcstTime == newItem.fcstTime
            }

            override fun areContentsTheSame(oldItem: WeatherModel.ForecastModel, newItem: WeatherModel.ForecastModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}