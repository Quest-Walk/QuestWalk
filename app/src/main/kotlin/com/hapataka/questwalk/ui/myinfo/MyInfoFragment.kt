package com.hapataka.questwalk.ui.myinfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.core.designsystem.R.drawable
import com.hapataka.questwalk.core.designsystem.component.PixelButton
import com.hapataka.questwalk.core.designsystem.component.QuestWalkTopAppBar
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.ui.component.Character
import com.hapataka.questwalk.ui.LoginActivity
import com.hapataka.questwalk.ui.myinfo.component.InfoContent
import com.hapataka.questwalk.ui.myinfo.dialog.DropOutDialog
import com.hapataka.questwalk.ui.myinfo.dialog.InputPwDialog
import com.hapataka.questwalk.core.ui.UiState
import com.hapataka.questwalk.util.extentions.DETAIL_TIME
import com.hapataka.questwalk.util.extentions.convertKcal
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInfoFragment : Fragment() {
    private val viewModel by viewModels<MyInfoViewModel>()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val inputPwDialog by lazy { InputPwDialog() }
    private val dropOutDialog by lazy { DropOutDialog() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            requireActivity().enableEdgeToEdge()
            setContent {
                QuestWalkTheme(lightBar = true) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) { padding ->
                        MyInfoScreen(
                            popBackStack = navController::popBackStack,
                            navigateToLogin = {
                                val intent = Intent(requireContext(), LoginActivity::class.java)

                                startActivity(intent)
                                requireActivity().finish()
                            },
                            padding = padding
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyInfoScreen(
    popBackStack: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
    viewModel: MyInfoViewModel = hiltViewModel()
) {
    val userState by viewModel.user.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(loginState) {
        if (loginState.not()) navigateToLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        QuestWalkTopAppBar(
            title = "내 정보",
            contentColor = MainPurple,
            leadingIcon = ImageVector.vectorResource(drawable.ic_back),
            onClickLeadingIcon = popBackStack
        )

        when (userState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    Modifier
                        .fillMaxWidth(0.3f)
                        .weight(1f)
                        .align(Alignment.CenterHorizontally),
                    color = MainPurple
                )
            }

            is UiState.Success -> {
                MyInfoContent(
                    user = (userState as UiState.Success<User>).data,
                    onLogoutClick = viewModel::logout
                )
            }

            else -> {}
        }

    }

}

@Composable
private fun MyInfoContent(
    user: User,
    onLogoutClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var animationState by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Character(
                isAnimate = animationState,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
                    .clickable {
                        animationState = animationState.not()
                    }
            )
            Text(
                text = user.userName,
                style = Typography.bodyMedium,
                color = Color.Black,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            InfoContent(
                title = "모험한 시간",
                content = user.totalTime.convertTime(DETAIL_TIME),
            )

            InfoContent(
                title = "모험한 거리",
                content = user.totalDistance.convertKm(),
            )

            InfoContent(
                title = "총 걸음수",
                content = "${user.totalStep}걸음",
                label = user.totalStep.convertKcal()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                InfoContent(
                    title = "해결한 퀘스트",
                    content = "${user.successKeywords.size}개",
                    modifier = Modifier.weight(1f)
                )

                InfoContent(
                    title = "달성한 업적",
                    content = "${user.achievementIds.size}개",
                    modifier = Modifier.weight(1f)
                )
            }

            PixelButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onLogoutClick,
                text = "로그아웃"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyInfoScreenPreview() {
    MyInfoScreen()
}

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        loadInitialSetting()
//        initView()
//    }
//
//    private fun loadInitialSetting() {
//        setObserver()
//        binding.innerContainer.setPadding()
//        requireActivity().setLightBarColor(true)
//        viewModel.getCurrentUserInfo()
//        viewModel.getHistoryCount()
//    }
//
//    private fun initView() {
//        initButtons()
//    }
//
//    private fun setObserver() {
//        with(viewModel) {
//            currentUser.observe(viewLifecycleOwner) { user ->
//                with(binding) {
//                    tvPlayerName.text = user.nickName
//                    tvStepValue.text = "${user.totalStep}걸음"
//                    tvDistanceValue.text = user.totalDistance.convertKm()
//                    tvCalorie.text = user.totalStep.convertKcal()
//                    tvTimeValue.text = user.totalTime.convertTime(DETAIL_TIME)
//                }
//            }
//            historyCount.observe(viewLifecycleOwner) { countMap ->
//                binding.tvSolveQuestValue.text = countMap[RESULT_SUCCESS_COUNT].toString() + "개"
//                binding.tvAchieveCouunt.text = countMap[ACHIEVEMENT_COUNT].toString() + "개"
//            }
//            logoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
//                if (isSuccess) {
//                    lifecycleScope.launch {
//                        val intent = Intent(
//                            requireContext(),
//                            LoginActivity::class.java
//                        )
//
//                        startActivity(intent)
//                        delay(1000L)
//                        requireActivity().finish()
//                    }
//                }
//            }
//            btnState.observe(viewLifecycleOwner) { isEnable ->
//                if (isEnable) {
//                    binding.btnLogout.isEnabled = true
//                }
//            }
//            toastMsg.observe(viewLifecycleOwner) { msg ->
//                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//            }
//            reauthSuccess.observe(viewLifecycleOwner) { isSuccess ->
//                if (isSuccess) {
//                    inputPwDialog.dismiss()
//                    showDropOutDialog()
//                }
//            }
//            dropOutSuccess.observe(viewLifecycleOwner) { isSuccess ->
//                if (isSuccess) {
//                    lifecycleScope.launch {
//                        dropOutDialog.dismiss()
//                        val intent = Intent(
//                            requireContext(),
//                            LoginActivity::class.java
//                        )
//
//                        startActivity(intent)
//                        delay(1000L)
//                        requireActivity().finish()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun initButtons() {
//        with(binding) {
//            btnBack.setOnClickListener {
//                navController.popBackStack()
//            }
//            btnPlayerName.setOnClickListener {
//                showEditNickNameDialog()
//            }
//            btnDropOut.setOnClickListener {
//                showReauthDialog()
//            }
//            btnLogout.setOnClickListener {
//                viewModel.logout()
//                it.isEnabled = false
//            }
//        }
//    }
//
//
//    private fun showEditNickNameDialog() {
//        val currentName = binding.tvPlayerName.text.toString()
//        val dialogFragment = EditNickNameDialog(currentName)
//
//        dialogFragment.onNicknameChanged = { newNickname ->
//            when {
//                newNickname.isEmpty() -> {
//                    Toast.makeText(requireContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
//                    Result.failure(Exception("No Input Name"))
//                }
//
//                newNickname == currentName -> {
//                    Toast.makeText(requireContext(), "변경된 정보가 없습니다", Toast.LENGTH_SHORT).show()
//                    Result.failure(Exception("Nickname not changed"))
//                }
//
//                else -> {
//                    viewModel.changeUserNickName(newNickname)
//                    binding.tvPlayerName.text = newNickname
//                    Toast.makeText(requireContext(), "유저정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
//                    Result.success(true)
//                }
//            }
//        }
//        dialogFragment.show(parentFragmentManager, "EditNickNameDialog")
//    }
//
//    private fun showReauthDialog() {
//        inputPwDialog.apply {
//            onInputPw = { pw ->
//                if (pw.isEmpty()) {
//                    Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
//                } else {
//                    viewModel.reauthCurrentUser(pw)
//                }
//            }
//        }.show(parentFragmentManager, "InputPwDialog")
//    }
//
//    private fun showDropOutDialog() {
//        dropOutDialog.apply {
//            onConfirm = {
//                viewModel.dropOutCurrentUser()
//            }
//        }.show(parentFragmentManager, "DropOutDialog")
//    }
//    // TODO: 캐릭터 변경 구현
//}