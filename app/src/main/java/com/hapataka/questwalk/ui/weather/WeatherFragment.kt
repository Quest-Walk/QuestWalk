package com.hapataka.questwalk.ui.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentWeatherBinding
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapter
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapterDecoration

class WeatherFragment : Fragment() {
    private val binding by lazy { FragmentWeatherBinding.inflate(layoutInflater) }
    private val weatherAdapter by lazy { WeatherAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        with(binding.revWeather) {
            adapter = weatherAdapter
            addItemDecoration(WeatherAdapterDecoration())
        }
    }
}