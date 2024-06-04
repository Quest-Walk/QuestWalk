package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.databinding.ActivitySplashSceneBinding
import com.hapataka.questwalk.domain.facade.UserFacade
import com.hapataka.questwalk.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashSceneActivity : BaseActivity<ActivitySplashSceneBinding>(ActivitySplashSceneBinding::inflate) {
    @Inject lateinit var userFacade: UserFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBackPressedCallback()
        lifecycleScope.launch {
            val user = userFacade.getLoginUserToken()

            delay(2000L) // wait for splash scene

            if (user == null) {
                changeTo(LoginActivity::class.java)
            } else {
//                changeTo(MainActivity::class.java)
                changeTo(LoginActivity::class.java)
            }
        }
    }

    private fun <T> changeTo(activity: Class<T>) {
        val intent = Intent(this, activity)

        startActivity(intent)
        finish()
    }

    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        }.also {
            onBackPressedDispatcher.addCallback(this, it)
        }
    }

}