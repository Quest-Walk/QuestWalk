package com.hapataka.questwalk.ui.login

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentLogInBinding
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.main.MainActivity
import com.hapataka.questwalk.util.extentions.hideKeyBoard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : BaseFragment<FragmentLogInBinding>(FragmentLogInBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
        exitTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitialSetting()
        initViews()
    }

    private fun loadInitialSetting() {
        initObserver()
        loadInfo()
    }

    private fun initObserver() {
        with(viewModel) {
            userId.observe(viewLifecycleOwner) { userId ->
                binding.etLoginId.setText(userId)
            }

            loginResult.observe(viewLifecycleOwner) { loginResult ->
                if (loginResult) changeToMain()
            }

            toastMsg.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }

            loginBtnState.observe(viewLifecycleOwner) { isActive ->
                if (isActive) {
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun loadInfo() {
        requireActivity().setLightBarColor(true)
        initBackPressedCallback()
        viewModel.getIdFromPref()
    }

    private fun initViews() {
        initLoginButton()
        initSignUpButton()
        initFindPassWordButton()
    }

    private fun initLoginButton() {
        with(binding) {
            btnLogin.setOnClickListener {
                val id = etLoginId.text.toString()
                val pw = etLoginPassword.text.toString()

                it.isEnabled = false
                viewModel.loginByIdAndPw(id, pw)
                hideKeyBoard()
            }
        }
    }

    private fun initFindPassWordButton() {
        binding.tvFindPassWord.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_findPassWordFragment)
        }
    }

    private fun initSignUpButton() {
        binding.btnSignUp.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_frag_sign_up)
        }
    }

    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish()
                    return
                }
                backPressedOnce = true
                Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }.also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }

    private fun changeToMain() {
        lifecycleScope.launch {
            val intent = Intent(requireContext(), MainActivity::class.java)

            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle()
            )
            delay(1000L)
            requireActivity().finish()
        }
    }
}