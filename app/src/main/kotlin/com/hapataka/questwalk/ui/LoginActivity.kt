package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hapataka.questwak.feature.MainScreen
import com.hapataka.questwalk.core.navigation.OnboardingRoute
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isLogin = intent.getBooleanExtra("isLogin", false)

        setContent {
            val intent = Intent(this, MainActivity::class.java)
            val startDestination =
                if (isLogin) OnboardingRoute.Setup else OnboardingRoute.Login

            MainScreen(
                startDestination = startDestination,
                navigateToHome = {
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

