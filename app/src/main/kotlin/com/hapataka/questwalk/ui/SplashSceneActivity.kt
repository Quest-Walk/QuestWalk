package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.databinding.ActivitySplashSceneBinding
import com.hapataka.questwalk.ui.common.BaseActivity
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashSceneActivity :
    BaseActivity<ActivitySplashSceneBinding>(ActivitySplashSceneBinding::inflate) {
    private val viewModel: SplashSceneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackPressedCallback()

        lifecycleScope.launch {
            viewModel.loginState.collectLatest { userState ->
                when (userState) {
                    is com.hapataka.questwalk.feature.onboarding.model.UserState.LoggedIn -> {
                        if (userState.userInfo == com.hapataka.questwalk.feature.onboarding.model.UserInfo.EXIST) {
                            viewModel.cacheCurrentUserHistories()
                            changeTo(
                                activity = MainActivity::class.java
                            )
                        } else {
                            changeTo(
                                activity = LoginActivity::class.java, isLoggedIn = true
                            )
                        }
                    }

                    is com.hapataka.questwalk.feature.onboarding.model.UserState.LoggedOut -> {
                        changeTo(LoginActivity::class.java)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun <T> changeTo(
        activity: Class<T>,
        isLoggedIn: Boolean = false,
    ) {
        val intent = Intent(this, activity).apply {
            putExtra("isLoggedIn", isLoggedIn)
        }

        startActivity(intent)
        finish()
    }

    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }.also {
            onBackPressedDispatcher.addCallback(this, it)
        }
    }
}