package com.hapataka.questwalk.ui.myinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentMyInfoBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchievementEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.ui.login.showSnackbar
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory

class MyInfoFragment : BaseFragment<FragmentMyInfoBinding>(FragmentMyInfoBinding::inflate) {
    private val myInfoViewModel by viewModels<MyInfoViewModel> { ViewModelFactory() }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val navGraph by lazy { navController.navInflater.inflate(R.navigation.nav_graph) }

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
            userInfo.observe(viewLifecycleOwner) { userInfo ->
                updateViewsWithUserInfo(userInfo)
            }
        }
    }

    private fun updateViewsWithUserInfo(userInfo: UserEntity) {
        val history = userInfo.histories
        val achieveCount = history.filterIsInstance<AchievementEntity>().size
        val successResultCount = history.filterIsInstance<ResultEntity>().filter { !it.isFailed }.size.toString()

        with(binding) {
            tvPlayerName.text = userInfo.nickName
            tvStepValue.text = userInfo.totalStep.toString() + " 걸음"
            tvDistanceValue.text = userInfo.totalDistance.toString() + " Km"
            tvAchieveCouunt.text = "$achieveCount 개"
            tvSolveQuestValue.text ="$successResultCount 개"
            tvTimeValue.text = userInfo.totalTime
            tvCalorie.text = (userInfo.totalStep * 0.06f).toString() + " kcal"
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