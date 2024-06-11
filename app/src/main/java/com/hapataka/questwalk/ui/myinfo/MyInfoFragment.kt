package com.hapataka.questwalk.ui.myinfo

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentMyInfoBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.ui.LoginActivity
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.main.MainViewModel
import com.hapataka.questwalk.ui.myinfo.dialog.DropOutDialog
import com.hapataka.questwalk.ui.myinfo.dialog.EditNickNameDialog
import com.hapataka.questwalk.ui.myinfo.dialog.InputPwDialog
import com.hapataka.questwalk.ui.onboarding.CharacterData
import com.hapataka.questwalk.ui.onboarding.ChooseCharacterDialog
import com.hapataka.questwalk.ui.onboarding.OnCharacterSelectedListener
import com.hapataka.questwalk.util.UserInfo
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertKcal
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyInfoFragment : BaseFragment<FragmentMyInfoBinding>(FragmentMyInfoBinding::inflate) {
    private val viewModel by viewModels<MyInfoViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val navGraph by lazy { navController.navInflater.inflate(R.navigation.nav_graph) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitialSetting()
        initView()

    }

    private fun loadInitialSetting() {
        setObserver()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
        viewModel.getCurrentUserInfo()
    }

    private fun initView() {
        initLogoutButton()
        initButtons()

        initDropOut()
    }

    private fun setObserver() {
        with(viewModel) {
            currentUser.observe(viewLifecycleOwner) { user ->
                with(binding) {
                    tvPlayerName.text = user.nickName
                    tvStepValue.text = "${user.totalStep}걸음"
                    tvDistanceValue.text = user.totalDistance.convertKm()
                    tvCalorie.text = user.totalStep.convertKcal()
                    tvTimeValue.text = user.totalTime.convertTime(DETAIL_TIME)
                }
            }
            logoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    lifecycleScope.launch {
                        val intent = Intent(requireContext(), LoginActivity::class.java)

                        startActivity(
                            intent,
                            ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                                .toBundle()
                        )
                        delay(1000L)
                        requireActivity().finish()
                    }
                }
            }
            btnState.observe(viewLifecycleOwner) { isEnable ->
                if (isEnable) {
                    binding.btnLogout.isEnabled = true
                }
            }
        }

        with(viewModel) {
            userInfo.observe(viewLifecycleOwner) { userInfo ->
                updateViewsWithUserInfo(userInfo)
            }
        }
    }

    private fun initLogoutButton() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            it.isEnabled = false
        }
    }

    private fun initButtons() {
        with(binding) {
            btnBack.setOnClickListener {
                navController.popBackStack()
            }
            btnPlayerName.setOnClickListener {
                showEditNickNameDialog()
            }
            ivPlayerCharacter.setOnClickListener {
                showCharacterDialog()
            }
        }
    }



    private fun updateViewsWithUserInfo(userInfo: UserEntity) {
        val history = userInfo.histories
        val achieveCount = history.filterIsInstance<AchieveResultEntity>().size
        val successResultCount =
            history.filterIsInstance<ResultEntity>().filterNot { it.isSuccess }.size.toString()

        with(binding) {
            tvAchieveCouunt.text = "$achieveCount 개"
            tvSolveQuestValue.text = "$successResultCount 개"


            when (userInfo.characterId) {
                1 -> ivPlayerCharacter.setImageResource(R.drawable.character_01)
                else -> ivPlayerCharacter.setImageResource(R.drawable.character_01)
            }
        }
    }
    lateinit var inputPwDialog: InputPwDialog

    private fun initDropOut() {
        binding.btnDropOut.setOnClickListener {
            inputPwDialog = InputPwDialog(
                reauthCallback = { pw ->
                    viewModel.reauthCurrentUser(
                        pw,
                        positiveCallback = {
                            showDropOutDialog()
                            inputPwDialog.dismiss()
                        },
                        negativeCallback = {
                            mainViewModel.setSnackBarMsg("비밀번호를 확인해주세요.")
                        }
                    )
                },

                snackBarCallback = {
                    mainViewModel.setSnackBarMsg(it)
                    Log.i("permission_test", "$it")
                }
            )
            inputPwDialog.show(parentFragmentManager, "input_pw_dialog")
        }
    }

    private fun showDropOutDialog() {
        DropOutDialog(
            dropOutCallback = {
                viewModel.deleteCurrentUser() {
                    navController.navigate(R.id.action_frag_my_info_to_frag_login)
                    navGraph.setStartDestination(R.id.frag_home)
                    navController.graph = navGraph
                }
            }
        ).show(parentFragmentManager, "dropOut Dialog")
    }

    private fun showCharacterDialog() {
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
                mainViewModel.setSnackBarMsg("로그인 상태를 확인할 수 없습니다.")
            }
        }
    }

    private fun updateUserInfo(userId: String, characterNum: Int) {
        val nickName = binding.tvPlayerName.text.toString()
        viewModel.setUserInfo(userId, characterNum, nickName,
            onSuccess = { mainViewModel.setSnackBarMsg("변경완료") },
            onError = { mainViewModel.setSnackBarMsg("정보 변경에 실패하였습니다.") })
    }

    private fun showEditNickNameDialog() {
        val currentName = binding.tvPlayerName.text.toString()
        val dialogFragment = EditNickNameDialog(currentName)

        dialogFragment.onNicknameChanged = { newNickname ->
            if (newNickname == currentName) {
                Toast.makeText(requireContext(), "변경된 정보가 없습니다", Toast.LENGTH_SHORT).show()
                Result.failure(Exception("Nickname not changed"))
            } else {
                viewModel.changeUserNickName(newNickname)
                binding.tvPlayerName.text = newNickname
                Toast.makeText(requireContext(), "유저정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                Result.success(true)
            }
        }
        dialogFragment.show(parentFragmentManager, "EditNickNameDialog")
    }

    private fun updateNickName(newNickname: String) {
        viewModel.getUserCharacterNum { characterNum ->
            val uid = UserInfo.uid
            val charNum = characterNum ?: 1
            viewModel.setUserInfo(uid, charNum, newNickname,
                onSuccess = {
                    mainViewModel.setSnackBarMsg("닉네임이 성공적으로 변경되었습니다.")
                    binding.tvPlayerName.text = newNickname
                },
                onError = { mainViewModel.setSnackBarMsg("정보 변경에 실패하였습니다.") })
        }
    }
}