package com.hapataka.questwalk.ui.signup

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentSignUpBinding
import com.hapataka.questwalk.ui.login.showSnackbar
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.extentions.showErrMsg

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {
    private val viewModel: SignUpViewModel by viewModels { SignUpViewModelFactory(AuthRepositoryImpl()) }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var isCanClick = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.slide_right)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initEditText()
        initSignUpButton()
        initCloseButton()
        initPwVisibilityButton()
    }

    private fun initEditText() {
        with(binding) {
            etSignUpId.resetMsg(tvExplainId, "아이디는 이메일 형식으로 입력해 주세요.")
            etSignUpPw.resetMsg(tvExplainPw, "비밀번호는 6자 이상으로 입력해주세요.")
            etSignUpCheckPw.resetMsg(tvExplainPw, "비밀번호는 6자 이상으로 입력해주세요.")
        }
    }

    private fun initSignUpButton() {
        with(binding) {
            btnSignUp.setOnClickListener {
                hideKeyBoard()
                if (!isCanClick) return@setOnClickListener
                isCanClick = false

                val emailId = etSignUpId.text.toString()
                val pw = etSignUpPw.text.toString()
                val pwCheck = etSignUpCheckPw.text.toString()


                if (checkEmailValidity(emailId)) {
                    etSignUpId.requestFocus()
                    isCanClick = true
                    return@setOnClickListener
                }

                if (checkPwValidity(pw, pwCheck)) {
                    etSignUpPw.requestFocus()
                    isCanClick = true
                    return@setOnClickListener
                }

                viewModel.registerByEmailAndPw(emailId, pw,
                    { moveHomeWithLogin(emailId, pw) },
                    { ("이미 가입된 아이디입니다.").showSnackbar(requireView())
                    isCanClick = true })
            }

            Handler(Looper.getMainLooper()).postDelayed({
                isCanClick = true
            },1500)
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
        Log.d(TAG, "id: $id")

        if (id.isEmpty()) {
            binding.tvExplainId.showErrMsg("이메일 주소를 입력해주세요.", requireContext())
            return true
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
            binding.tvExplainId.showErrMsg("이메일 형식이 올바르지 않습니다.", requireContext())
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
            { navigateToOnBoarding() }, { ("오류가 발생해 로그인을 할 수 없습니다.").showSnackbar(requireView()) })

    }

    private fun navigateToOnBoarding() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        exitTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
        navController.navigate(R.id.action_frag_sign_up_to_frag_on_boarding)
        navGraph.setStartDestination(R.id.frag_on_boarding)
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



//    private fun showPasswordVisibility(icon: ImageView, editText: EditText) {
//        icon.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    editText.transformationMethod =
//                        HideReturnsTransformationMethod.getInstance()
//                    editText.setSelection(editText.text.length)
//                }
//
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    editText.transformationMethod =
//                        PasswordTransformationMethod.getInstance()
//                    editText.setSelection(editText.text.length)
//                }
//            }
//            true
//        }
    }

    private fun hideKeyBoard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
}

