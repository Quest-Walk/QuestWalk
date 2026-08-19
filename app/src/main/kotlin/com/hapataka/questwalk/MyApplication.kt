package com.hapataka.questwalk

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initOpenCv()
        initMaps()
    }

    /**
     * 지도 렌더러 준비를 앱 시작 시점으로 당긴다.
     * 하지 않으면 결과 화면에서 지도를 처음 띄울 때 한 번 크게 끊긴다.
     */
    private fun initMaps() {
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) { }
    }

    private fun initOpenCv() {
        OpenCVLoader.initDebug()
    }
}
