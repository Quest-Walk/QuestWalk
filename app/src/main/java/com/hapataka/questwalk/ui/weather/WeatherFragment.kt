package com.hapataka.questwalk.ui.weather

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.databinding.FragmentWeatherBinding
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapter
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapterDecoration
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment<FragmentWeatherBinding>(FragmentWeatherBinding::inflate){
    private val weatherViewModel: WeatherViewModel by viewModels {ViewModelFactory(requireContext())}
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val weatherAdapter by lazy { WeatherAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataObserve()
        initRecyclerView()
        initBackButton()
    }

    private fun dataObserve() {
        with(weatherViewModel) {
            weatherInfo.observe(viewLifecycleOwner) {
                weatherAdapter.submitList(it)
            }
            dustInfo.observe(viewLifecycleOwner) {
                binding.tvMiseValue.text = "${it.pm10Value} ㎍/㎥"
                binding.tvChomiseValue.text = "${it.pm25Value} ㎍/㎥"
            }
        }
    }

    private fun initRecyclerView() {
        with(binding.revWeather) {
            adapter = weatherAdapter
            addItemDecoration(WeatherAdapterDecoration())
        }
    }

    private fun initBackButton() {
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }
}