package com.hapataka.questwalk.ui.fragment.myinfo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentMyInfoBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.ui.activity.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.fragment.myinfo.dialog.DropOutDialog
import com.hapataka.questwalk.ui.fragment.myinfo.dialog.InputPwDialog
import com.hapataka.questwalk.ui.fragment.myinfo.dialog.NickNameChangeDialog
import com.hapataka.questwalk.ui.fragment.onboarding.CharacterData
import com.hapataka.questwalk.ui.fragment.onboarding.ChooseCharacterDialog
import com.hapataka.questwalk.ui.fragment.onboarding.OnCharacterSelectedListener
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.UserInfo
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertKcal
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime

class MyInfoFragment : BaseFragment<FragmentMyInfoBinding>(FragmentMyInfoBinding::inflate) {
    private val viewModel by viewModels<MyInfoViewModel> { ViewModelFactory(requireContext()) }
    private val mainViewModel: MainViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val navGraph by lazy { navController.navInflater.inflate(R.navigation.nav_graph) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        viewModel.getUserInfo()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
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
                mainViewModel.setSnackBarMsg(msg)
            }
            userInfo.observe(viewLifecycleOwner) { userInfo ->
                updateViewsWithUserInfo(userInfo)
            }
        }
    }

    private fun updateViewsWithUserInfo(userInfo: UserEntity) {
        val history = userInfo.histories
        val achieveCount = history.filterIsInstance<AchieveResultEntity>().size
        val successResultCount =
            history.filterIsInstance<ResultEntity>().filterNot { it.isSuccess }.size.toString()
        val time = userInfo.totalTime.toLongOrNull()
        val defaultNickName = "이름없는 모험가"

        with(binding) {
            tvPlayerName.text =
                if (userInfo.nickName.isBlank()) defaultNickName else userInfo.nickName
            tvStepValue.text = userInfo.totalStep.toString() + " 걸음"
            tvDistanceValue.text = userInfo.totalDistance.convertKm()
            tvAchieveCouunt.text = "$achieveCount 개"
            tvSolveQuestValue.text = "$successResultCount 개"
            tvCalorie.text = userInfo.totalStep.convertKcal()

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

    private fun changeName() {
        binding.tvPlayerName.setOnClickListener {
            startEditNameDialog()
        }
    }

    private fun startEditNameDialog() {
        val currentName = binding.tvPlayerName.text.toString()
        val dialogFragment = NickNameChangeDialog(currentName).apply {
            onNicknameChanged = { newNickname ->
                updateNickName(newNickname)
            }
        }
        dialogFragment.show(parentFragmentManager, "NickNameChangeDialog")
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