package com.hapataka.questwalk.ui.result

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.util.BaseFragment

class ResultFragment : BaseFragment<FragmentResultBinding>(FragmentResultBinding::inflate),
    OnMapReadyCallback {
    private val resultViewModel: ResultViewModel by viewModels()
    private var successItem: QuestData.SuccessItem? = null
    private var completeRate: Double? = null
    private lateinit var result: HistoryEntity.ResultEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            successItem = it.getParcelable("SuccessItem") as? QuestData.SuccessItem
            completeRate = it.getDouble("CompleteRate")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataObserve()
        getInfo()

        binding.fragMap.onCreate(savedInstanceState)
        binding.fragMap.getMapAsync(this)
        Log.d("check", "onviewcreated")
    }

    private fun dataObserve() {
        resultViewModel.resultItem.observe(viewLifecycleOwner) {
            initViews(it)
            resultViewModel.getQuestByKeyword(it.quest)
        }

        resultViewModel.questItem.observe(viewLifecycleOwner) {
            initImageViews(it)
        }
    }

    private fun getInfo() {
        successItem?.let {
            resultViewModel.getResultHistory(it.userId, it.imageUrl)
        }
    }

    private fun initViews(result: HistoryEntity.ResultEntity) {
        with(binding) {
            ivQuestImage.load(result.questImg)
//            tvAdvTime.text = result.time
            tvAdvDistance.text = "${result.distance}"
            tvTotalSteps.text = "${result.step}"
            tvCalories.text = "Zero"
            tvQuestKeyword.text = result.quest
            tvCompleteRate.text = "$completeRate"
        }
        this.result =result
    }

    private fun initImageViews(questItem: QuestData) {
        val imageList = listOf(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3,
            binding.ivImage4
        )

        for (i in imageList.indices) {
            if (i < questItem.successItems.size) imageList[i].visibility = View.VISIBLE
            else imageList[i].visibility = View.INVISIBLE
        }

        questItem.successItems.reversed().take(4).forEachIndexed { index, successItem ->
            imageList[index].load(successItem.imageUrl)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.d("check", "onmapready")
        MapsInitializer.initialize(this.requireContext())
        updateLocation(p0, result)
    }

    private fun updateLocation(p0: GoogleMap, result: HistoryEntity.ResultEntity) {
        Log.d("check", result.toString())
        var preLocation : Pair<Float, Float>? = null
        val resultLati = result.locations?.map { it.first } ?: listOf()
        val resultLongi = result.locations?.map {it.second} ?: listOf()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
            LatLngBounds(
                LatLng(resultLati.minOf{it}.toDouble(),resultLongi.minOf{it}.toDouble()),
                LatLng(resultLati.maxOf { it }.toDouble(), resultLongi.maxOf { it }.toDouble()),
            ), 100
        )
        p0.animateCamera(cameraUpdate)

        for (location in resultLati.zip(resultLongi)){
            Log.d("위치정보",  "위도: ${location.first.toDouble()} 경도: ${location.second.toDouble()}")
            if(preLocation!=null){
                Log.d("check", "${location.first.toDouble()} ${location.second.toDouble()} ${preLocation?.first?.toDouble()} ${preLocation?.second?.toDouble()}")
                var polyline = p0.addPolyline(
                    PolylineOptions()
                        .clickable(true)
                        .add(
                            LatLng(preLocation!!.first.toDouble(), preLocation!!.second.toDouble()),
                            LatLng(location.first.toDouble(), location.second.toDouble())
                        )
                )
                polyline.width = 15.0F
                polyline.color = Color.BLACK
                polyline.jointType = JointType.ROUND
                polyline.startCap=RoundCap()
                polyline.endCap= RoundCap()
            }
            preLocation=location
        }
    }
}