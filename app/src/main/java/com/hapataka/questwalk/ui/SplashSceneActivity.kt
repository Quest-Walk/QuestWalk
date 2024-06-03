package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.databinding.ActivitySplashSceneBinding
import com.hapataka.questwalk.domain.facade.UserFacade
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashSceneActivity : AppCompatActivity() {
    private var _binding: ActivitySplashSceneBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userFacade: UserFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashSceneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val user = userFacade.getLoginUserToken()

            delay(2000L) // wait for splash scene

            if (user == null) {
                val intentToLogin = Intent(this@SplashSceneActivity, LoginActivity::class.java)

                startActivity(intentToLogin)
                finish()
            } else {
                val intentToMain = Intent(this@SplashSceneActivity, MainActivity::class.java)

                startActivity(intentToMain)
                finish()
            }
        }
    }
}