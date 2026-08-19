package com.hapataka.questwalk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initOpenCv()
    }

    private fun initOpenCv() {
        OpenCVLoader.initDebug()
    }
}