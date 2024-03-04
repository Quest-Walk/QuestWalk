package com.hapataka.questwalk.ui.weather

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentWeatherBinding
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapter
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapterDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private val binding by lazy { FragmentWeatherBinding.inflate(layoutInflater) }
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val weatherAdapter by lazy { WeatherAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataObserve()
        initRecyclerView()
    }

    private fun dataObserve() {
        weatherViewModel.weatherInfo.observe(viewLifecycleOwner) {
            Log.d("WeatherFragment:","weatherInfo: $it")
            weatherAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        with(binding.revWeather) {
            adapter = weatherAdapter
            addItemDecoration(WeatherAdapterDecoration())
        }
    }
}