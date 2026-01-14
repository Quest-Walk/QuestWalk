package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.feature.quest.QuestRoute
import com.hapataka.questwalk.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment shell for hosting feature:quest's QuestRoute
 */
@AndroidEntryPoint
class QuestFragment : Fragment() {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireActivity()).apply {
            requireActivity().enableEdgeToEdge()
            setContent {
                QuestWalkTheme(lightBar = false) {
                    Scaffold(modifier = Modifier.background(Color.White)) { paddingValues ->
                        CompositionLocalProvider(
                            LocalPaddingValues provides paddingValues
                        ) {
                            QuestRoute(
                                onBackClick = { navController.popBackStack() },
                                onQuestDetailClick = { keyword ->
                                    val bundle = Bundle().apply {
                                        putString("keyword", keyword)
                                    }
                                    navController.navigate(R.id.action_frag_quest_to_frag_quest_detail, bundle)
                                },
                                onQuestSelected = { keyword ->
                                    mainViewModel.setSelectKeyword(keyword)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}