package com.hapataka.questwalk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hapataka.questwalk.databinding.ActivityMainBinding

const val TAG = "test_tag"

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
