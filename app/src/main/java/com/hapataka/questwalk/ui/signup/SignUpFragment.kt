package com.hapataka.questwalk.ui.signup

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.OnBoardingFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentSignUpBinding
import com.hapataka.questwalk.record.TAG
import kotlinx.coroutines.launch


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null

    private val binding get() = _binding!!
    private val viewModel: SignUpViewModel by viewModels()
    private val authRepo by lazy { AuthRepositoryImpl() }

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
        goNext()
        showPasswordVisibility(binding.ivShowPassWord, binding.etSignUpPassWord)
        showPasswordVisibility(binding.ivShowPassWordCheck, binding.etSignUpPassWordCheck)
    }

    fun TextView.showError(msg: String) {
        val animShake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake_error)

        text = msg
        startAnimation(animShake)
    }


    private fun goNext() {
        binding.btnGoToPassWord.setOnClickListener {
            val emailId = binding.etSignUpId.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
                Toast.makeText(context, "이메일 형식이 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                changePassWordUi()
            }
        }

        binding.btnGoToJoin.setOnClickListener {
            val email = binding.etSignUpId.text.toString()
            val password = binding.etSignUpPassWord.text.toString()
            val passwordCheck = binding.etSignUpPassWordCheck.text.toString()

            if (password == passwordCheck) {
                lifecycleScope.launch {
                    authRepo.registerByEmailAndPw(email, password) { task ->
                        if (task.isSuccessful) {
                            moveHomeWithLogin(email, password)
                            return@registerByEmailAndPw
                        }
                        binding.tvErrorMsg.showError("가입불가능하네요")
                    }
                }
            } else {
                Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveHomeWithLogin(id: String, pw: String) {
        lifecycleScope.launch {
            authRepo.loginByEmailAndPw(id, pw) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_frag_sign_up_to_frag_home)
                }
                Log.e(TAG, "에러남")
            }
        }
    }

    private fun obeserver() {
        viewModel.signUpResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                switchFragment(requireFragmentManager(), OnBoardingFragment(), false)
            }
        }
    }

    fun switchFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        addToBackStack: Boolean
    ) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(com.google.android.material.R.id.container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }


    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    private fun changePassWordUi() {
        binding.apply {
            tvId.text = "비밀번호"
            tvExplain.text = "비밀번호는 특수문자를 포함해 주세요"
            etSignUpId.isVisible = false
            btnGoToPassWord.visibility = View.INVISIBLE
            etSignUpPassWord.isVisible = true
            etSignUpPassWordCheck.isVisible = true
            btnGoToJoin.isVisible = true
            binding.ivShowPassWord.isVisible = true
            binding.ivShowPassWordCheck.isVisible = true
        }
    }

    private fun showPasswordVisibility(icon: ImageView, editText: EditText) {
        icon.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    editText.setSelection(editText.text.length)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    editText.transformationMethod = PasswordTransformationMethod.getInstance()
                    editText.setSelection(editText.text.length)
                }
            }
            true
        }
    }


    private fun changeIdUi() {
        binding.apply {
            tvId.text = "아이디"
            tvExplain.text = "아이디는 이메일 형식으로 입력해 주세요"
            etSignUpId.isVisible = true
            btnGoToPassWord.isVisible = true
            etSignUpPassWord.isVisible = false
            etSignUpPassWordCheck.isVisible = false
            btnGoToJoin.isVisible = false
            binding.ivShowPassWord.isVisible = false
            binding.ivShowPassWordCheck.isVisible = false
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}