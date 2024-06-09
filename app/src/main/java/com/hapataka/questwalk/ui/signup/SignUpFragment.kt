package com.hapataka.questwalk.ui.signup

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentSignUpBinding
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.login.TAG
import com.hapataka.questwalk.ui.main.MainActivity
import com.hapataka.questwalk.util.extentions.hideKeyBoard
import com.hapataka.questwalk.util.extentions.setOnFocusOutListener
import com.hapataka.questwalk.util.extentions.showErrMsg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {
    private val viewModel: SignUpViewModel by viewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.slide_right)
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
            isValidId.observe(viewLifecycleOwner) { state ->
                binding.tvExplainId.showErrMsg(
                    context = requireContext(),
                    msg = when (state) {
                        EMPTY_INPUT -> {
                            "아이디를 입력해주세요."
                        }

                        NOT_EMAIL_TYPE -> {
                            "아이디는 이메일 형식으로 입력해 주세요."
                        }

                        else -> ""
                    }
                )
            }

            isValidPw.observe(viewLifecycleOwner) { state ->
                binding.tvExplainPw.showErrMsg(
                    context = requireContext(),
                    msg = when (state) {
                        EMPTY_INPUT -> {
                            "비밀번호를 입력해주세요."
                        }

                        SHORT_PW -> {
                            "비밀번호는 6자 이상으로 입력해 주세요."
                        }

                        CONFIRM_PW_EMPTY -> {
                            "비밀번호를 확인해주세요."
                        }

                        PW_NOT_MATCH -> {
                            "비밀번호가 일치하지 않습니다."
                        }

                        else -> ""
                    }
                )
            }

            toastMsg.observe(viewLifecycleOwner) { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }

            isRegisterSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    changeToMain()
                }
            }

            signUpBtnState.observe(viewLifecycleOwner) { state ->
                Log.d(TAG, "state: $state")
                if (state) {
                    binding.btnSignUp.isEnabled = true
                }
            }
        }
    }

    private fun loadInfo() {
        requireActivity().setLightBarColor(true)
    }

    private fun initViews() {
        initSignUpButton()
        initInputField()
        initPwVisibilityButton()
        initCloseButton()
        checkTypeAllElements()
    }

    private fun initInputField() {
        with(binding) {
            etSignUpId.setOnFocusOutListener {
                viewModel.validateId(it.text.toString())
            }
            etSignUpPw.setOnFocusOutListener {
                viewModel.validatePw(etSignUpPw.text.toString(), etSignUpCheckPw.text.toString())
            }
            etSignUpCheckPw.setOnFocusOutListener {
                viewModel.validatePw(etSignUpPw.text.toString(), etSignUpCheckPw.text.toString())
            }
        }
    }

    private fun initSignUpButton() {
        with(binding) {
            btnSignUp.setOnClickListener {
                val id = etSignUpId.text.toString()
                val pw = etSignUpPw.text.toString()
                val confirmPw = etSignUpCheckPw.text.toString()

                btnSignUp.isEnabled = false
                viewModel.registerByIdAndPw(id, pw, confirmPw)
                hideKeyBoard()
            }
        }
    }

    private fun initCloseButton() {
        binding.btnClose.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun initPwVisibilityButton() {
        binding.ivShowPassWord.toggleVisibility(binding.etSignUpPw)
        binding.ivShowPassWordCheck.toggleVisibility(binding.etSignUpCheckPw)
    }

    private fun ImageView.toggleVisibility(editText: EditText) {
        val password = PasswordTransformationMethod.getInstance()
        val hide = HideReturnsTransformationMethod.getInstance()

        setOnClickListener {
            with(editText) {
                if (transformationMethod == password) {
                    load(R.drawable.ic_pw_show_enable)
                    transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    setSelection(text.length)
                    return@setOnClickListener
                }

                if (transformationMethod == hide) {
                    load(R.drawable.ic_pw_show_disable)
                    transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    setSelection(text.length)
                    return@setOnClickListener
                }
            }
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

    private fun checkTypeAllElements() {
        val editTexts = listOf(
            binding.etSignUpId,
            binding.etSignUpPw,
            binding.etSignUpCheckPw
        )

        editTexts.forEach { editText ->
            editText.doAfterTextChanged {
                if (editTexts.any { it.text.isEmpty() }) {
                    binding.btnSignUp.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.gray))
                } else {
                    binding.btnSignUp.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.main_purple))
                }
            }
        }
    }
}

