package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.core.model.LoginState
import com.hapataka.questwalk.core.model.UserInfo
import com.hapataka.questwalk.databinding.ActivitySplashSceneBinding
import com.hapataka.questwalk.ui.common.BaseActivity
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
            viewModel.userState.collectLatest { userState ->
                delay(500)

                when (userState) {
                    is LoginState.Success -> {
                        if (userState.userInfo == UserInfo.EXIST) {
                            viewModel.cacheCurrentUserHistories()
                            changeTo(activity = MainActivity::class.java)
                        } else {
                            changeTo(
                                activity = LoginActivity::class.java, isLogin = true
                            )
                        }
                    }

                    is LoginState.Failure -> {
                        changeTo(LoginActivity::class.java)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun <T> changeTo(
        activity: Class<T>,
        isLogin: Boolean = false,
    ) {
        val intent = Intent(this, activity).apply {
            putExtra("isLogin", isLogin)
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