package com.hapataka.questwalk.data.map

import android.graphics.Color
import android.util.Log
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

class GoogleMapRepositoryImpl: MapRepository, OnMapReadyCallback {
//    private var googleMap: GoogleMap? = null
    private lateinit var locationList: MutableList<LatLng>

    override fun drawPath(result: HistoryEntity.ResultEntity) {
        Log.d("drawPath Call:","drawPath")
        // 위도 경도 받고
        locationList = result.locations?.map {
            LatLng(it.first.toDouble(), it.second.toDouble())
        }?.toMutableList() ?: mutableListOf()

        googleMap?.addPolyline(
            PolylineOptions()
                .clickable(false)
                .addAll(locationList)
                .width(15.0F)
                .color(Color.rgb(122, 94, 200))
                .jointType(JointType.ROUND)
                .startCap(RoundCap())
                .endCap(RoundCap())
        )
        addMarker(result)
        controlCamera()
    }

    private fun addMarker(result: HistoryEntity.ResultEntity) {
        // questLocation 받고
        val questLatitude = result.questLocation?.first?.toDouble() ?: 0.0
        val questLongitude = result.questLocation?.second?.toDouble() ?: 0.0

        googleMap?.addMarker(
            MarkerOptions()
                .title("QUEST")
                .position(LatLng(questLatitude, questLongitude))
        )
    }

    private fun controlCamera() {
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
            LatLngBounds(
                LatLng(locationList.minOf { it.latitude }, locationList.minOf { it.longitude }),
                LatLng(locationList.maxOf { it.latitude }, locationList.maxOf { it.longitude }),
            ), 100
        )
        googleMap?.animateCamera(cameraUpdate)
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
    }

    companion object {
        private var googleMap: GoogleMap? = null
    }
}