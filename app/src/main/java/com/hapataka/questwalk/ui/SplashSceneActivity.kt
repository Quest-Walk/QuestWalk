package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.databinding.ActivitySplashSceneBinding
import com.hapataka.questwalk.ui.mainactivity.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashSceneActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashSceneBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(2000L)
            val intent = Intent(this@SplashSceneActivity, MainActivity::class.java)

            startActivity(intent)
            finish()
        }

    }
}