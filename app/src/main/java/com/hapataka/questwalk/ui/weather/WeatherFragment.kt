package com.hapataka.questwalk.ui.weather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
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
        with(weatherViewModel) {
            weatherModel.observe(viewLifecycleOwner) {weatherModel ->
                Log.d("weatherFragment:","weatherModel: $weatherModel")
                weatherAdapter.submitList(weatherModel.forecastList)
                initDustViews(weatherModel)
            }

            weatherState.observe(viewLifecycleOwner) {weatherState ->
                Log.d("weatherFragment:","weatherState: $weatherState")
            }

            error.observe(viewLifecycleOwner) {error ->
                if (error) setErrorState()
            }

            isLoading.observe(viewLifecycleOwner) {isLoading ->
                showLoadingDialog(isLoading)
            }
        }
    }

    private fun initViews() {
        initRecyclerView()
        initBackButton()

        weatherViewModel.getWeatherModel()
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

    private fun setWeatherPreview(weatherPreview: WeatherPreviewData) {
        binding.tvMessage.text =
            "현재 온도는 ${weatherPreview.currentTmp}도 이고, 하늘 상태는 ${weatherPreview.sky} ${weatherPreview.precipType} "+
                    "미세먼지 상태는 ${weatherPreview.miseState} 초미세 먼지 상태는 ${weatherPreview.choMiseState} 오늘 여행에 참고하라구!!"
    }

    private fun setDustText(dustInfo: DustEntity) {
        with(binding) {
            tvMiseValue.text = if (dustInfo.pm10Value == -1) "통신 장애" else "${dustInfo.pm10Value} ㎍/㎥"
            tvChomiseValue.text = if (dustInfo.pm25Value == -1) "통신 장애" else "${dustInfo.pm25Value} ㎍/㎥"
        }
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
}