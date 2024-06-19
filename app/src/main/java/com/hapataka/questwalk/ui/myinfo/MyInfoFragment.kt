package com.hapataka.questwalk.ui.myinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.databinding.FragmentMyInfoBinding
import com.hapataka.questwalk.domain.facade.ACHIEVEMENT_COUNT
import com.hapataka.questwalk.domain.facade.RESULT_SUCCESS_COUNT
import com.hapataka.questwalk.ui.LoginActivity
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.myinfo.dialog.DropOutDialog
import com.hapataka.questwalk.ui.myinfo.dialog.EditNickNameDialog
import com.hapataka.questwalk.ui.myinfo.dialog.InputPwDialog
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
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val inputPwDialog by lazy { InputPwDialog() }
    private val dropOutDialog by lazy { DropOutDialog() }

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
        viewModel.getHistoryCount()
    }

    private fun initView() {
        initButtons()
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
            historyCount.observe(viewLifecycleOwner) { countMap ->
                binding.tvSolveQuestValue.text = countMap[RESULT_SUCCESS_COUNT].toString() + "개"
                binding.tvAchieveCouunt.text = countMap[ACHIEVEMENT_COUNT].toString() + "개"
            }
            logoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    lifecycleScope.launch {
                        val intent = Intent(requireContext(), LoginActivity::class.java)

                        startActivity(intent)
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
            toastMsg.observe(viewLifecycleOwner) { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
            reauthSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    inputPwDialog.dismiss()
                    showDropOutDialog()
                }
            }
            dropOutSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    lifecycleScope.launch {
                        dropOutDialog.dismiss()
                        val intent = Intent(requireContext(), LoginActivity::class.java)

                        startActivity(intent)
                        delay(1000L)
                        requireActivity().finish()
                    }
                }
            }
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
            btnDropOut.setOnClickListener {
                showReauthDialog()
            }
            btnLogout.setOnClickListener {
                viewModel.logout()
                it.isEnabled = false
            }
        }
    }


    private fun showEditNickNameDialog() {
        val currentName = binding.tvPlayerName.text.toString()
        val dialogFragment = EditNickNameDialog(currentName)

        dialogFragment.onNicknameChanged = { newNickname ->
            when {
                newNickname.isEmpty() -> {
                    Toast.makeText(requireContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    Result.failure(Exception("No Input Name"))
                }

                newNickname == currentName -> {
                    Toast.makeText(requireContext(), "변경된 정보가 없습니다", Toast.LENGTH_SHORT).show()
                    Result.failure(Exception("Nickname not changed"))
                }

                else -> {
                    viewModel.changeUserNickName(newNickname)
                    binding.tvPlayerName.text = newNickname
                    Toast.makeText(requireContext(), "유저정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    Result.success(true)
                }
            }
        }
        dialogFragment.show(parentFragmentManager, "EditNickNameDialog")
    }

    private fun showReauthDialog() {
        inputPwDialog.apply {
            onInputPw = { pw ->
                if (pw.isEmpty()) {
                    Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.reauthCurrentUser(pw)
                }
            }
        }.show(parentFragmentManager, "InputPwDialog")
    }

    private fun showDropOutDialog() {
        dropOutDialog.apply {
            onConfirm = {
                viewModel.dropOutCurrentUser()
            }
        }.show(parentFragmentManager, "DropOutDialog")
    }
    // TODO: 캐릭터 변경 구현
}