package com.hapataka.questwalk.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentOnBoardingBinding
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSetupFragment :
    BaseFragment<FragmentOnBoardingBinding>(FragmentOnBoardingBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var characterNum = 1
    private val mainViewModel: MainViewModel by activityViewModels ()
    private val viewModel: ProfileSetupViewModel by viewModels<ProfileSetupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun init() {
        changeProfile()
        goMain()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
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
            hideKeyBoard()

            val nickName = binding.etNickname.text.toString()

            if (nickName.isEmpty()) {
                mainViewModel.setSnackBarMsg("닉네임을 입력해 주세요")
                return@setOnClickListener
            }
            viewModel.updateUserName(nickName)
            navController.popBackStack()
        }
    }



//                viewModel.getCurrentUserId { userId ->
//                    if (userId.isNotEmpty()) {

//                        viewModel.setUserInfo(userId,
//                            characterNum,
//                            nickName,
//                            { navigateGoHome() },
//                            onError = { error -> mainViewModel.setSnackBarMsg(error) })
//                    } else {
//                        mainViewModel.setSnackBarMsg("로그인 상태를 확인할 수 없습니다.")
//                    }
//                }
//            }
//        })

    private fun navigateGoHome() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        navController.navigate(R.id.action_frag_on_boarding_to_frag_home)
        navGraph.setStartDestination(R.id.frag_home)
        navController.graph = navGraph
    }

    private fun hideKeyBoard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
}