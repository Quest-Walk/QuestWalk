package com.hapataka.questwalk.data.map

import android.animation.ValueAnimator
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.repository.MapRepository

class GoogleMapRepositoryImpl : MapRepository, OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private lateinit var locationList: MutableList<LatLng>

    override fun drawPath(result: HistoryEntity.ResultEntity) {
        locationList = result.locations?.map {
            LatLng(it.first.toDouble(), it.second.toDouble())
        }?.toMutableList() ?: mutableListOf()

        while(locationList.size>1000){
            locationList =
                locationList.filterIndexed { index, latLng -> index % 2 == 0 }.toMutableList()
        }

        val tAnimator = ValueAnimator.ofInt(0, if (locationList.size < 2) 0 else locationList.size - 2 ).apply {
            duration = (locationList.size * 50).toLong()
            addUpdateListener { tAnimator ->
                val frame = tAnimator.animatedValue as Int
                val nextFrameValue = if (locationList.size < 2) {
                    LatLng(locationList[frame].latitude + 0.000001, locationList[frame].longitude + 0.000001)
                } else {
                    LatLng(locationList[frame + 1].latitude, locationList[frame + 1].longitude)
                }

                val polyline = googleMap?.addPolyline(
                    PolylineOptions().add(
                        LatLng(locationList[frame].latitude, locationList[frame].longitude),
                        nextFrameValue
                    )
                )

                polyline?.width = 15.0F
                polyline?.color = Color.rgb(122, 94, 200)
                polyline?.jointType = JointType.ROUND
                polyline?.startCap = RoundCap()
                polyline?.endCap = RoundCap()
            }
        }
        tAnimator.startDelay = 800
        tAnimator.start()

        addMarker(result)
        controlCamera()
    }

    private fun addMarker(result: HistoryEntity.ResultEntity) {
        val questLatitude = result.questLocation?.first?.toDouble() ?: 0.0
        val questLongitude = result.questLocation?.second?.toDouble() ?: 0.0

        googleMap?.addMarker(
            MarkerOptions()
                .title("QUEST")
                .position(LatLng(questLatitude, questLongitude))
        )
    }

    private fun controlCamera() {
        val bounds = LatLngBounds.Builder().apply {
            locationList.forEach { include(it) }
        }.build()
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        p0.uiSettings.isZoomControlsEnabled = true
    }
}