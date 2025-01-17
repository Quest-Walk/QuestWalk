package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hapataka.questwak.feature.MainScreen
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.navigation.Route
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val noUserInfo = intent.getBooleanExtra("noUserInfo", false)

        setContent {
            val intent = Intent(this, MainActivity::class.java)
            val startDestination =
                if (noUserInfo) Route.Onboarding(OnboardingStep.Setup) else Route.Onboarding(
                    OnboardingStep.Login
                )

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

