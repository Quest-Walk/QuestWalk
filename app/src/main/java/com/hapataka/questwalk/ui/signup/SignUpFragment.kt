package com.hapataka.questwalk.ui.signup

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentSignUpBinding
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.login.TAG
import com.hapataka.questwalk.ui.main.MainViewModel
import com.hapataka.questwalk.util.extentions.hideKeyBoard
import com.hapataka.questwalk.util.extentions.showErrMsg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {
    private val viewModel: SignUpViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
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
        viewModel.isValidId.observe(viewLifecycleOwner) {
            binding.tvExplainId.showErrMsg(
                context = requireContext(),
                msg = when (it) {
                    EMPTY_INPUT -> "아이디를 입력해주세요."
                    NOT_EMAIL_TYPE -> "아이디는 이메일 형식으로 입력해 주세요."
                    else -> ""
                }
            )
        }
    }

    private fun loadInfo() {
        requireActivity().setLightBarColor(true)
    }

    private fun initViews() {
        initInputField()
        initSignUpButton()

        initPwVisibilityButton()
        initCloseButton()
    }

    fun <T : View> T.setOnFocusOutListener(action: (T) -> Unit) {
        setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                action(v as T)
            }
        }
    }

    private fun initInputField() {
        binding.etSignUpId.setOnFocusOutListener {
            viewModel.validateId(it.text.toString())
        }
        binding.etSignUpPw.setOnFocusOutListener {
            Log.d(TAG, "포커스 아웃")
        }
        binding.etSignUpCheckPw.setOnFocusOutListener {
            Log.d(TAG, "포커스 아웃")
        }
//        with(binding) {
//            etSignUpId.resetMsg(tvExplainId, "아이디는 이메일 형식으로 입력해 주세요.")
//            etSignUpPw.resetMsg(tvExplainPw, "비밀번호는 6자 이상으로 입력해주세요.")
//            etSignUpCheckPw.resetMsg(tvExplainPw, "비밀번호는 6자 이상으로 입력해주세요.")
//        }
//        checkTypeAllElements()
    }

    private fun initSignUpButton() {
        with(binding) {
            btnSignUp.setOnClickListener {
                val id = etSignUpId.text.toString()
                val pw = etSignUpPw.text.toString()
                val pwCheck = etSignUpCheckPw.text.toString()

                hideKeyBoard()
            }
        }
    }

    private fun EditText.resetMsg(textView: TextView, msg: String) {
        doAfterTextChanged {
            textView.text = msg
            textView.setTextColor(resources.getColor(R.color.main_purple))
        }
    }

    private fun initCloseButton() {
        binding.btnClose.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun checkEmailValidity(id: String): Boolean {
        if (id.isEmpty()) {
            binding.tvExplainId.showErrMsg("이메일 주소를 입력해주세요.", requireContext())
            return true
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
            binding.tvExplainId.showErrMsg("아이디는 이메일 형식으로 입력해주세요.", requireContext())
            return true
        }
        return false
    }

    private fun checkPwValidity(pw: String, pwCheck: String): Boolean {
        if (pw != pwCheck) {
            binding.tvExplainPw.showErrMsg("비밀번호가 일치하지 않습니다.", requireContext())
            return true
        }

        if (pw.length < 6) {
            binding.tvExplainPw.showErrMsg("비밀번호는 6자리 이상으로 설정해 주세요.", requireContext())
            return true
        }
        return false
    }

    private fun moveHomeWithLogin(id: String, pw: String) {
        viewModel.logByEmailAndPw(id, pw,
            { navigateToOnBoarding() }, { mainViewModel.setSnackBarMsg("오류가 발생해 로그인을 할 수 없습니다.") })
    }

    private fun navigateToOnBoarding() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        exitTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
        navController.navigate(R.id.action_frag_sign_up_to_frag_on_boarding)
        navGraph.setStartDestination(R.id.frag_home)
        navController.graph = navGraph
    }

    private fun initPwVisibilityButton() {
        binding.ivShowPassWord.toggleVisibility(binding.etSignUpPw)
        binding.ivShowPassWordCheck.toggleVisibility(binding.etSignUpCheckPw)
    }

    private fun ImageView.toggleVisibility(editText: EditText) {
        val passwod = PasswordTransformationMethod.getInstance()
        val hide = HideReturnsTransformationMethod.getInstance()

        setOnClickListener {
            with(editText) {
                if (transformationMethod == passwod) {
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

