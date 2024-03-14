package com.hapataka.questwalk.ui.result

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.map.GoogleMapRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertKcal
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime

const val USER_ID = "user_id"
const val QUEST_KEYWORD = "quest_keyword"
const val REGISTER_TIME = "register_time"

class ResultFragment : BaseFragment<FragmentResultBinding>(FragmentResultBinding::inflate){
    private val viewModel: ResultViewModel by viewModels { ViewModelFactory() }
    private var userId: String? = null
    private var keyword: String? = null
    private var registerAt: String? = null
    private val mapRepo by lazy { GoogleMapRepositoryImpl() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
            keyword = it.getString(QUEST_KEYWORD)
            registerAt = it.getString(REGISTER_TIME)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMapView(savedInstanceState)
        getInfo()
        setObserver()
    }

    private fun initMapView(bundle: Bundle?) {
        binding.fragMap.onCreate(bundle)
        binding.fragMap.getMapAsync(mapRepo)
    }

    private fun getInfo() {
        if (userId != null && keyword != null && registerAt != null) {
            viewModel.getResult(userId!!, keyword!!, registerAt!!)
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
            if (result.isFailed.not()) {
                ivQuestImage.load(result.questImg)
            } else {
                ivQuestImage.load(R.drawable.image_fail)
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
            }
        }
    }
}