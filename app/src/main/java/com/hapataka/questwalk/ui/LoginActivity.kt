package com.hapataka.questwalk.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.hapataka.designsystem.theme.QuestWalkTheme
import com.hapataka.login.LoginRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuestWalkTheme(false) {
                Scaffold { padding ->
                    LoginRoute(padding)
                }
            }
        }
    }
}