package com.hapataka.questwalk.ui.weather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.data.model.WeatherModel
import com.hapataka.questwalk.databinding.FragmentWeatherBinding
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapter
import com.hapataka.questwalk.ui.weather.adapter.WeatherAdapterDecoration
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.util.LoadingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment : BaseFragment<FragmentWeatherBinding>(FragmentWeatherBinding::inflate){
    private val weatherViewModel: WeatherViewModel by viewModels ()
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val weatherAdapter by lazy { WeatherAdapter(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(false)
        dataObserve()
        initViews()
    }

    private fun dataObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherUiState.collectLatest { state ->
                    when(state) {
                        is WeatherUiState.Loading -> {
                            showLoadingDialog(state.loading)
                        }
                        is WeatherUiState.Success -> {
                            weatherAdapter.submitList(state.data.forecastList)
                            initDustViews(state.data)
                            Log.d("weatherFragment:","weatherState: ${state.level}")
                        }
                        is WeatherUiState.Error -> {
                            setErrorState()
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        initRecyclerView()
        initBackButton()
    }

    private fun initRecyclerView() {
        with(binding.revWeather) {
            adapter = weatherAdapter
            addItemDecoration(WeatherAdapterDecoration())
        }
    }

    private fun initDustViews(weathertModel: WeatherModel) {
        with(binding) {
            tvMiseValue.text = weathertModel.pm10Value.toString()
            tvChomiseValue.text = weathertModel.pm25Value.toString()
        }
    }

    private fun setErrorState() {
        Toast.makeText(requireContext(), "통신 장애로 인해 날씨 정보를 불러오지 못했습니다. 잠시후 다시 시도 해주세요", Toast.LENGTH_SHORT).show()
        navHost.popBackStack()
    }

    private fun showLoadingDialog(isLoading: Boolean) {
        if (isLoading) {
            LoadingDialogFragment().show(parentFragmentManager, "loadingDialog")
        } else {
            val loadingFragment =
                parentFragmentManager.findFragmentByTag("loadingDialog") as? LoadingDialogFragment
            loadingFragment?.dismiss()
        }
    }

    private fun initBackButton() {
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

}