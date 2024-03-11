package com.hapataka.questwalk.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentOnBoardingBinding
import com.hapataka.questwalk.ui.login.showSnackbar
import com.hapataka.questwalk.util.BaseFragment


class OnBoardingFragment :
    BaseFragment<FragmentOnBoardingBinding>(FragmentOnBoardingBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val viewModel: OnBoardingViewModel by viewModels {
        OnBoardingViewModelFactory(
            UserRepositoryImpl(),
            AuthRepositoryImpl()
        )
    }
    private var characterNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        changeProfile()
        goMain()
    }

    private fun changeProfile() {
        binding.ivProfileImage.setOnClickListener {
            val dialogFragment = ChooseCharacterDialog().apply {
                listener = object : OnCharacterSelectedListener {
                    override fun onCharacterSelected(characterData: CharacterData) {
                        binding.ivProfileImage.setImageResource(characterData.img)
                        characterNum = characterData.num
                    }
                }
            }
            dialogFragment.show(requireFragmentManager(), "ChooseCharacterDialog")
        }
    }

    private fun goMain() {
        binding.btnComplete.setOnClickListener {
            val nickName = binding.etNickname.text.toString()

            if (nickName.isEmpty()) {
                ("닉네임을 입력해 주세요").showSnackbar(requireView())
                return@setOnClickListener
            }

            viewModel.getCurrentUserId { userId ->
                if (userId.isNotEmpty()) {
                    viewModel.setUserInfo(userId,
                        characterNum,
                        nickName,
                        { navigateGoHome() },
                        onError = { error -> error.showSnackbar(requireView()) })
                } else {
                    ("로그인 상태를 확인할 수 없습니다.").showSnackbar(requireView())
                }
            }

//            lifecycleScope.launch {
//                val userId = authRepo.getCurrentUserUid()
//                userRepo.setUserInfo(userId,characterNum,nickName)
//            }
        }
    }

    private fun navigateGoHome() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        navController.navigate(R.id.action_frag_on_boarding_to_frag_home)
        navGraph.setStartDestination(R.id.frag_home)
        navController.graph = navGraph
    }
}