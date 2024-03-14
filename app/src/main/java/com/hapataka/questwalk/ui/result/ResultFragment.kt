package com.hapataka.questwalk.ui.result

import android.animation.ValueAnimator
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertKcal
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime


const val USER_ID = "user_id"
const val QUEST_KEYWORD = "quest_keyword"
const val REGISTER_TIME = "register_time"

class ResultFragment : BaseFragment<FragmentResultBinding>(FragmentResultBinding::inflate),
    OnMapReadyCallback {
    private val viewModel: ResultViewModel by viewModels { ViewModelFactory() }
    private var userId: String? = null
    private var keyword: String? = null
    private var registerAt: String? = null
    private lateinit var googleMap: GoogleMap
//    private lateinit var result: HistoryEntity.ResultEntity

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
        binding.fragMap.onCreate(savedInstanceState)
        binding.fragMap.getMapAsync(this)

        getInfo()
        setObserver()
    }

    private fun getInfo() {
        if (userId != null && keyword != null && registerAt != null) {
            viewModel.getResult(userId!!, keyword!!, registerAt!!)
        }
    }

    private fun setObserver() {
        with(viewModel) {
            resultItem.observe(viewLifecycleOwner) {
                if (::googleMap.isInitialized) {
                    MapsInitializer.initialize(requireContext())
                    updateLocation(googleMap, it)
                }
                initViews(it)
                viewModel.getQuestByKeyword(it.quest)
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

    override fun onMapReady(p0: GoogleMap) {
        Log.i(TAG, "map ready")
        googleMap = p0
        p0.uiSettings.isZoomControlsEnabled=true
        MapsInitializer.initialize(this.requireContext())
//        updateLocation(googleMap, result)
    }

    private fun updateLocation(p0: GoogleMap, result: HistoryEntity.ResultEntity) {
        Log.d("check", result.toString())
        var preLocation: Pair<Float, Float>? = null
        var destinationLocation: Pair<Float, Float>? = null
        val resultLati = result.locations?.map { it.first } ?: listOf()
        val resultLongi = result.locations?.map { it.second } ?: listOf()
        val resultIterator = resultLati.zip(resultLongi).iterator()
        if (resultLati.any() && resultLongi.any()){
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
                LatLngBounds(
                    LatLng(resultLati.minOf { it }.toDouble(), resultLongi.minOf { it }.toDouble()),
                    LatLng(resultLati.maxOf { it }.toDouble(), resultLongi.maxOf { it }.toDouble()),
                ), 100
            )
            p0.animateCamera(cameraUpdate)
        }

        preLocation = resultIterator.next()
        destinationLocation = resultIterator.next()
        val tAnimator = ValueAnimator.ofInt(0, resultLongi.size-2)
        tAnimator.duration = 5000
        val frameDistance = result.distance
        tAnimator.addUpdateListener {
            // animate here
            tAnimator ->
            val frame = tAnimator.animatedValue as Int
            val polyline = p0.addPolyline(
                PolylineOptions()
                    .add(
                        LatLng(resultLati[frame].toDouble(), resultLongi[frame].toDouble()),
                        LatLng(resultLati[frame+1].toDouble(), resultLongi[frame+1].toDouble())
                    )
                    .addSpan(
                        StyleSpan(
                            StrokeStyle.gradientBuilder(
                                Color.HSVToColor(floatArrayOf(360F*frame/(resultLati.size-1), 1F, 1F)),
                                Color.HSVToColor(floatArrayOf(360F*(frame+1)/(resultLati.size-1), 1F, 1F))
                            ).build()
                        )
                    )
            )
            polyline.width = 15.0F
//            polyline.color = Color.rgb(122, 94, 200)
            polyline.jointType = JointType.ROUND
            polyline.startCap = RoundCap()
            polyline.endCap = RoundCap()
        }
        tAnimator.startDelay=3000
        tAnimator.start()

//        for (location in resultLati.zip(resultLongi)) {
//            Log.d(TAG + "위치정보", "위도: ${location.first.toDouble()} 경도: ${location.second.toDouble()}")
//            if (preLocation != null) {
//                Log.d(
//                    TAG + "check",
//                    "${location.first.toDouble()} ${location.second.toDouble()} ${preLocation.first.toDouble()} ${preLocation.second.toDouble()}"
//                )
//                val polyline = p0.addPolyline(
//                    PolylineOptions()
//                        .add(
//                            LatLng(preLocation.first.toDouble(), preLocation.second.toDouble()),
//                            LatLng(location.first.toDouble(), location.second.toDouble())
//                        )
//                )
//                polyline.width = 15.0F
//                polyline.color = Color.rgb(122, 94, 200)
//                polyline.jointType = JointType.ROUND
//                polyline.startCap = RoundCap()
//                polyline.endCap = RoundCap()
//            }
//            else{
//                val polyline = p0.addPolyline(
//                    PolylineOptions()
//                        .add(
//                            LatLng(location.first.toDouble()+0.000001, location.second.toDouble()+0.000001),
//                            LatLng(location.first.toDouble(), location.second.toDouble())
//                        )
//                )
//                polyline.width = 15.0F
//                polyline.color = Color.rgb(122, 94, 200)
//                polyline.jointType = JointType.ROUND
//                polyline.startCap = RoundCap()
//                polyline.endCap = RoundCap()
//            }
//            preLocation = location
//        }
    }
}