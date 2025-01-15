package com.hapataka.questwalk.ui.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.repository.GoogleMapRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertKcal
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import dagger.hilt.android.AndroidEntryPoint

const val RESULT_ID = "user_id"

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(FragmentResultBinding::inflate) {
    private val viewModel: ResultViewModel by viewModels()
    private var resultId: String? = null
    private val mapRepo by lazy { GoogleMapRepositoryImpl() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resultId = it.getString(RESULT_ID)
        }
        getInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMapView(savedInstanceState)
        setObserver()
        initBackButton()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun initMapView(bundle: Bundle?) {
        binding.fragMap.onCreate(bundle)
        binding.fragMap.getMapAsync(mapRepo)
    }

    private fun getInfo() {
        resultId?.let {
            viewModel.getResult(resultId!!)
        }
    }

    private fun setObserver() {
        with(viewModel) {
            resultItem.observe(viewLifecycleOwner) {
                initViews(it)
                mapRepo.drawPath(it)
            }
            questItem.observe(viewLifecycleOwner) {
                initImageViews(it)
            }
            completeRate.observe(viewLifecycleOwner) {
                binding.tvCompleteRate.text = "해결 인원 $it%"
            }
        }
    }

    private fun initViews(result: HistoryEntity.ResultEntity) {
        with(binding) {
            if (result.isSuccess) {
                ivQuestImage.load(result.questImg) {
                    placeholder(R.drawable.image_empty)
                    crossfade(true)
                    memoryCacheKey(result.questImg)
                }
            } else {
                ivQuestImage.visibility = View.GONE
            }
            tvAdvTime.text = result.time.convertTime(DETAIL_TIME)
            tvAdvDistance.text = result.distance.convertKm()
            tvTotalSteps.text = result.step.toString() + "걸음"
            tvCalories.text = result.step.convertKcal()
            tvQuestKeyword.text = result.quest
        }
    }

    private fun initImageViews(questItem: QuestData) {
        val imageList = listOf(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3,
            binding.ivImage4
        )

        imageList.forEach { it.load(R.drawable.image_empty) }

        questItem.successItems.reversed().take(4).forEachIndexed { index, successItem ->
            imageList[index].load(successItem.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.image_empty)
                memoryCacheKey(successItem.imageUrl)
            }
        }
    }

    private fun initBackButton() {
        binding.btnBack.setOnClickListener {
            parentFragment?.findNavController()?.popBackStack()
        }
    }
}