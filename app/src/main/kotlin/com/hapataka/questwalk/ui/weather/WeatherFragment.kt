package com.hapataka.questwalk.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.feature.weather.WeatherRoute
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment shell for hosting feature:weather's WeatherRoute
 */
@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireActivity()).apply {
            requireActivity().enableEdgeToEdge()
            setContent {
                QuestWalkTheme(lightBar = false) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                        CompositionLocalProvider(
                            LocalPaddingValues provides paddingValues
                        ) {
                            WeatherRoute(
                                onBackClick = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}
