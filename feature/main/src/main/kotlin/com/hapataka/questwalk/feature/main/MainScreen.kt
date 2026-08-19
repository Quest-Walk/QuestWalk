package com.hapataka.questwalk.feature.main

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import com.hapataka.questwalk.feature.main.navigation.MainNavHost
import com.hapataka.questwalk.feature.main.navigation.MainNavigator
import com.hapataka.questwalk.feature.main.navigation.rememberMainNavigator
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.navigation.MainRoute
import com.hapataka.questwalk.core.navigation.OnboardingStep
import com.hapataka.questwalk.core.ui.LocalPaddingValues

@Composable
fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    var lightBarEnable by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(navigator.navController) {
        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route?.substringAfterLast(".")
            lightBarEnable = route == OnboardingStep.Join::class.simpleName
        }
        navigator.navController.addOnDestinationChangedListener(listener)
        onDispose {
            navigator.navController.removeOnDestinationChangedListener(listener)
        }
    }

    // 홈은 백스택이 비어 있어 뒤로가기 한 번에 앱이 종료된다. 실수로 나가지 않도록 한 번 더 확인한다
    val context = LocalContext.current
    val isHome = navigator.currentDestination?.hasRoute(MainRoute.Home::class) == true
    var lastBackPressedAt by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = isHome) {
        val now = System.currentTimeMillis()
        if (now - lastBackPressedAt <= EXIT_CONFIRM_WINDOW_MILLIS) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressedAt = now
            Toast.makeText(context, "뒤로가기를 한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
    }

    QuestWalkTheme(lightBar = lightBarEnable) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars,
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalPaddingValues provides paddingValues
            ) {
                MainNavHost(
                    navigator = navigator,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private const val EXIT_CONFIRM_WINDOW_MILLIS = 2000L
