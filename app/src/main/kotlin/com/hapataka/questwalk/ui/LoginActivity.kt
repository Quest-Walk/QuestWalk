package com.hapataka.questwalk.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hapataka.questwak.feature.MainScreen
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val intent = Intent(this, MainActivity::class.java)

            QuestWalkTheme(false) {
                MainScreen(
                    navigateToHome = {
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

