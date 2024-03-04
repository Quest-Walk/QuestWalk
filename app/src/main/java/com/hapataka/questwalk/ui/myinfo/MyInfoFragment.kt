package com.hapataka.questwalk.ui.myinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentMyInfoBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.ui.login.showSnackbar
import com.hapataka.questwalk.util.ViewModelFactory

class MyInfoFragment : Fragment() {
    private val binding by lazy { FragmentMyInfoBinding.inflate(layoutInflater) }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val myInfoViewModel by viewModels<MyInfoViewModel> { ViewModelFactory() }
    private val navGraph by lazy { navController.navInflater.inflate(R.navigation.nav_graph) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        myInfoViewModel.getUserInfo()
    }

    private fun initView() {
        initLogoutButton()
        initDropOut()
        initBackButton()
    }

    private fun setObserver() {
        with(myInfoViewModel) {
            snackbarMsg.observe(viewLifecycleOwner) { msg ->
                msg.showSnackbar(requireView())
            }
            userInfo.observe(viewLifecycleOwner) { user ->
                with(binding) {
                    tvPlayerName.text = user.nickName
                    tvStepValue.text = user.totalStep.toString() + " 걸음"
                    tvDistanceValue.text = user.totalDistance.toString() + " Km"
                    tvAchieveCouunt.text =
                        user.histories.filterIsInstance<HistoryEntity.AchievementEntity>().size.toString() + " 개"
                    tvSolveQuestValue.text =
                        user.histories.filterIsInstance<HistoryEntity.ResultEntity>()
                            .filter { !it.isFailed }.size.toString() + " 개"
                    tvTimeValue.text = user.totalTime
                    tvCalorie.text = (user.totalStep * 0.06f).toString() + " kcal"
                }
            }
        }
    }

    private fun initLogoutButton() {
        binding.btnLogout.setOnClickListener {
            myInfoViewModel.logout {
                navController.navigate(R.id.action_frag_my_info_to_frag_login)
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
            }
        }
    }

    private fun initDropOut() {
        binding.btnDropOut.setOnClickListener {
            myInfoViewModel.leaveCurrentUser {
                navController.navigate(R.id.action_frag_my_info_to_frag_login)
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
            }
        }
    }

    private fun initBackButton() {
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }
}