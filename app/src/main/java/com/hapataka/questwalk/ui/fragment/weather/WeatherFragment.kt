package com.hapataka.questwalk.ui.fragment.weather

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.databinding.FragmentWeatherBinding
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.ui.fragment.weather.adapter.WeatherAdapter
import com.hapataka.questwalk.ui.fragment.weather.adapter.WeatherAdapterDecoration
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.LoadingDialogFragment
import com.hapataka.questwalk.util.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment<FragmentWeatherBinding>(FragmentWeatherBinding::inflate){
    private val weatherViewModel: WeatherViewModel by viewModels {ViewModelFactory(requireContext())}
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val weatherAdapter by lazy { WeatherAdapter(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataObserve()
        initRecyclerView()
        initBackButton()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(false)
    }

    private fun dataObserve() {
        with(weatherViewModel) {
            weatherInfo.observe(viewLifecycleOwner) {
                weatherAdapter.submitList(it)
            }
            dustInfo.observe(viewLifecycleOwner) {
                setDustText(it)
            }
            weatherPreview.observe(viewLifecycleOwner) {
                setWeatherPreview(it)
            }
            error.observe(viewLifecycleOwner) {
                setErrorState(it)
            }
            isLoading.observe(viewLifecycleOwner) {isLoading ->
                showLoadingDialog(isLoading)
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

    private fun setWeatherPreview(weatherPreview: WeatherPreviewData) {
        binding.tvMessage.text =
            "현재 온도는 ${weatherPreview.currentTmp}도 이고, 하늘 상태는 ${weatherPreview.sky} ${weatherPreview.precipType}" +
                    "미세먼지 상태는 ${weatherPreview.miseState} 초미세 먼지 상태는 ${weatherPreview.choMiseState} 오늘 여행에 참고하라구!!"
    }

    private fun setDustText(dustInfo: DustEntity) {
        with(binding) {
            tvMiseValue.text = "${dustInfo.pm10Value} ㎍/㎥"
            tvChomiseValue.text = "${dustInfo.pm25Value} ㎍/㎥"
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

    private fun setErrorState(error: String) {
        with(binding) {
            tvMessage.text = error
            tvMise.visibility = View.INVISIBLE
            tvChomise.visibility = View.INVISIBLE
            tvWeatherTime.visibility = View.INVISIBLE
            tvWeatherDetail.visibility = View.INVISIBLE
            ivArrowDown.visibility = View.INVISIBLE
            revWeather.visibility = View.INVISIBLE
        }


    }
}