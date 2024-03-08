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
import com.hapataka.questwalk.ui.onboarding.CharacterData
import com.hapataka.questwalk.ui.onboarding.ChooseCharacterDialog
import com.hapataka.questwalk.ui.onboarding.NickNameChangeDialog
import com.hapataka.questwalk.ui.onboarding.OnCharacterSelectedListener
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertTime

class MyInfoFragment : BaseFragment<FragmentMyInfoBinding>(FragmentMyInfoBinding::inflate) {
    private val viewModel by viewModels<MyInfoViewModel> { ViewModelFactory() }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val navGraph by lazy { navController.navInflater.inflate(R.navigation.nav_graph) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        viewModel.getUserInfo()

    }

    private fun initView() {
        initLogoutButton()
        initDropOut()
        initBackButton()
        changeCharacter()
        changeName()
    }

    private fun setObserver() {
        with(viewModel) {
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
        val successResultCount = history.filterIsInstance<ResultEntity>().filterNot { !it.isFailed }.size.toString()
        val time = userInfo.totalTime.toLongOrNull()

        with(binding) {
            tvPlayerName.text = userInfo.nickName
            tvStepValue.text = userInfo.totalStep.toString() + " 걸음"
            tvDistanceValue.text = userInfo.totalDistance.toString() + " Km"
            tvAchieveCouunt.text = "$achieveCount 개"
            tvSolveQuestValue.text ="$successResultCount 개"
            tvCalorie.text = (userInfo.totalStep * 0.06f).toString() + " kcal"

            when (userInfo.characterId) {
                 1 -> ivPlayerCharacter.setImageResource(R.drawable.character_01)
                else -> ivPlayerCharacter.setImageResource(R.drawable.character_01)
            }
            tvTimeValue.text = time?.convertTime(DETAIL_TIME) ?: "00시간 00분 00초"
        }
    }

    private fun initLogoutButton() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout {
                navController.navigate(R.id.action_frag_my_info_to_frag_login)
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
            }
        }
    }

    private fun initDropOut() {
        binding.btnDropOut.setOnClickListener {
            viewModel.leaveCurrentUser {
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

    private fun changeCharacter() {
        binding.ivPlayerCharacter.setOnClickListener {
            startCharacterDialog()
        }
    }

    private fun startCharacterDialog() {
        val dialogFragment = ChooseCharacterDialog().apply {
            listener = object : OnCharacterSelectedListener {
                override fun onCharacterSelected(characterData: CharacterData) {
                    updateCharacterInfo(characterData)
                }
            }
        }
        dialogFragment.show(requireFragmentManager(), "ChooseCharacterDialog")
    }

    private fun updateCharacterInfo(characterData: CharacterData) {
        binding.ivPlayerCharacter.setImageResource(characterData.img)
        viewModel.getCurrentUserId { userId ->
            if (userId.isNotEmpty()) {
                updateUserInfo(userId, characterData.num)
            } else {
                "로그인 상태를 확인할 수 없습니다.".showSnackbar(requireView())
            }
        }
    }

    private fun updateUserInfo(userId: String, characterNum: Int) {
        val nickName = binding.tvPlayerName.text.toString()
        viewModel.setUserInfo(userId, characterNum, nickName,
            onSuccess = { "변경완료".showSnackbar(requireView()) },
            onError = { "정보 변경에 실패하였습니다.".showSnackbar(requireView()) })
    }

    private fun changeName() {
        binding.tvPlayerName.setOnClickListener {
            startEditNameDialog()
        }
    }

    private fun startEditNameDialog() {
        val dialogFragment = NickNameChangeDialog().apply {
            onNicknameChanged = { newNickname ->
                updateNickName(newNickname)
            }
        }
        dialogFragment.show(parentFragmentManager,"NickNameChangeDialog")
    }

    private fun updateNickName(newNickname: String) {
        viewModel.getCurrentUserId { userId ->
            viewModel.getUserCharacterNum { characterNum ->
                val charNum = characterNum ?: 1
                viewModel.setUserInfo(userId, charNum, newNickname,
                    onSuccess = { ("닉네임이 성공적으로 변경되었습니다.").showSnackbar(requireView())
                    binding.tvPlayerName.text = newNickname },
                    onError ={"정보 변경에 실패하였습니다.".showSnackbar(requireView()) })
            }
        }
    }
}