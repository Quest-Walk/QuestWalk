package com.hapataka.questwalk.ui.signup

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentSignUpBinding
import com.hapataka.questwalk.ui.login.showSnackbar
import com.hapataka.questwalk.ui.record.TAG
import kotlinx.coroutines.launch
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val authRepo by lazy { AuthRepositoryImpl() }
    private val viewModel: SignUpViewModel by viewModels()
    private var isUiPassWord = false
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var emailId = ""
    private val passwordViews by lazy {
        listOf(
            binding.etSignUpPassWord,
            binding.etSignUpPassWordCheck,
            binding.btnGoToJoin,
            binding.ivShowPassWord,
            binding.ivShowPassWordCheck
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initGoToPasswordButton()
        initJoinButton()
        initPrevButton()
        showPasswordVisibility(binding.ivShowPassWord, binding.etSignUpPassWord)
        showPasswordVisibility(binding.ivShowPassWordCheck, binding.etSignUpPassWordCheck)
    }

//    fun TextView.showError(msg: String) {
//        val animShake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake_error)
//
//        text = msg
//        startAnimation(animShake)
//    }

    private fun initGoToPasswordButton() {
        with(binding) {
            btnGoToPassWord.setOnClickListener {
                emailId = etSignUpId.text.toString()
                hideKeyBoard()

                if (checkEmailValidity(emailId)) {
                    return@setOnClickListener
                }
                changePassWordUi()
            }
        }
    }

    private fun checkEmailValidity(id: String): Boolean {
        if (emailId.isEmpty()) {
            ("이메일 주소를 입력해주세요.").showSnackbar(requireView())
            return true
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            ("이메일 형식이 올바르지 않습니다.").showSnackbar(requireView())
            return true
        }
        return false
    }

    private fun initJoinButton() {
        with(binding) {
            btnGoToJoin.setOnClickListener {
                val password = etSignUpPassWord.text.toString()
                val passwordCheck = etSignUpPassWordCheck.text.toString()
                hideKeyBoard()

                if (password != passwordCheck) {
                    ("비밀번호가 일치하지 않습니다.").showSnackbar(requireView())
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    ("비밀번호는 6자리 이상으로 설정해 주세요.").showSnackbar(requireView())
                    return@setOnClickListener
                }


                lifecycleScope.launch {
                    authRepo.registerByEmailAndPw(emailId, password) { task ->
                        if (task.isSuccessful) {
                            moveHomeWithLogin(emailId, password)
                            return@registerByEmailAndPw
                        }
                        ("이미 가입된 아이디입니다.").showSnackbar(requireView())
                    }
                }
            }
        }
    }

    private fun moveHomeWithLogin(id: String, pw: String) {
        lifecycleScope.launch {
            authRepo.loginByEmailAndPw(id, pw) { task ->
                if (task.isSuccessful) {
                    val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

                    navController.navigate(R.id.action_frag_sign_up_to_frag_on_boarding)
                    navGraph.setStartDestination(R.id.frag_home)
//                    navGraph.setStartDestination(R.id.frag_on_boarding)
                    navController.graph = navGraph
                    return@loginByEmailAndPw
                }
                Log.e(TAG, "에러남")
            }
        }
    }



    private fun initPrevButton() {
        binding.btnBack.setOnClickListener {
            if (isUiPassWord) {
                changeIdUi()
                return@setOnClickListener
            }
            navController.popBackStack()
        }
    }



    private fun changePassWordUi() {
        binding.apply {
            tvId.text = "비밀번호"
            tvExplain.text = "비밀번호는 6글자 이상 입력해 주세요"
            etSignUpId.isVisible = false
            btnGoToPassWord.visibility = View.INVISIBLE
        }
        passwordViews.forEach { it.isVisible = true }
        isUiPassWord = true
    }

    private fun changeIdUi() {
        binding.apply {
            tvId.text = "아이디"
            tvExplain.text = "아이디는 이메일 형식으로 입력해 주세요"
            etSignUpId.isVisible = true
            btnGoToPassWord.isVisible = true
        }
        passwordViews.forEach { it.isVisible = false }
        isUiPassWord = false
    }

    private fun showPasswordVisibility(icon: ImageView, editText: EditText) {
        icon.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    editText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    editText.setSelection(editText.text.length)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    editText.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    editText.setSelection(editText.text.length)
                }
            }
            true
        }
    }

    private fun hideKeyBoard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

