package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.databinding.ActivitySplashSceneBinding
import com.hapataka.questwalk.domain.usecase.CacheCurrentUserUserCase
import com.hapataka.questwalk.domain.usecase.GetCacheUserUseCase
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashSceneActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashSceneBinding.inflate(layoutInflater) }
    @Inject
    lateinit var cache: CacheCurrentUserUserCase
    @Inject
    lateinit var getUser: GetCacheUserUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launch {

            async { cache.invoke() }.await()

            val user = getUser()



            delay(2000L)
            Log.d("down_user_test", "user: $user")
            val intent = Intent(this@SplashSceneActivity, MainActivity::class.java)

            startActivity(intent)
            finish()
        }

    }
}