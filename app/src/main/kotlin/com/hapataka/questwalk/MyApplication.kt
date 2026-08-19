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

    /** 지도 렌더러 선택을 앱 시작 시점으로 당긴다. */
    private fun initMaps() {
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) { }
    }

    private fun initOpenCv() {
        OpenCVLoader.initDebug()
    }
}
