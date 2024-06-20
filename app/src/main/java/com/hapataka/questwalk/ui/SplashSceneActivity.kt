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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashSceneActivity :
    BaseActivity<ActivitySplashSceneBinding>(ActivitySplashSceneBinding::inflate) {
    private val viewModel: SplashSceneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackPressedCallback()
        loadInitialSetting()
    }

    private fun loadInitialSetting() {
        viewModel.getCurrentUser()
        setObserver()
    }

    private fun setObserver() {
        with(viewModel) {
            currentUser.observe(this@SplashSceneActivity) { user ->
                lifecycleScope.launch {
                    if (user == null) {
                        changeTo(LoginActivity::class.java)
                    } else {
                        changeTo(MainActivity::class.java)
                        viewModel.cacheCurrentUserHistories()
                    }
                }

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
            override fun handleOnBackPressed() {}
        }.also {
            onBackPressedDispatcher.addCallback(this, it)
        }
    }

}