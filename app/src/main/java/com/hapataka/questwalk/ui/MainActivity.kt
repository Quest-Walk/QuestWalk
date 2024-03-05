package com.hapataka.questwalk.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hapataka.questwalk.ui.camera.CameraViewModel
import com.hapataka.questwalk.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "test_tag"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val cameraViewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
