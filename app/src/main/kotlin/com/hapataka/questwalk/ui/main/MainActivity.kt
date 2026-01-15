package com.hapataka.questwalk.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hapataka.questwak.feature.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initOpenCv()

        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        setContent {
            MainScreen(
                isLoggedIn = isLoggedIn,
            )
        }
    }

    private fun initOpenCv() {
        OpenCVLoader.initDebug()
    }
}
