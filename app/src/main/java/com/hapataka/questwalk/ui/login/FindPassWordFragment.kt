package com.example.firebasetestapp

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentFindPassWordBinding
import com.hapataka.questwalk.ui.main.MainViewModel
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.util.OnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPassWordFragment : BaseFragment<FragmentFindPassWordBinding>(FragmentFindPassWordBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val mainViewModel: MainViewModel by activityViewModels ()
    private var emailId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.slide_bottom)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSendEmailButton()
        goBackToLogIn()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun setUpSendEmailButton() {
        binding.btnSendEmail.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                sendEmail()
                hideKeyBoard()
            }
        })
    }

    private fun checkEmailValidity(id: String): Boolean {
        if (emailId.isEmpty()) {
            binding.tvWarning.showError("이메일을 입력해 주세요")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
            binding.tvWarning.showError("이메일 형식이 올바르지 않습니다.")
            return false
        }
        return true
    }

    private fun sendEmail() {
        emailId = binding.etFindEmail.text.toString()
        if (!checkEmailValidity(emailId)) return

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mainViewModel.setSnackBarMsg("이메일을 보냈습니다. 메일함을 확인해 주세요.")
                navController.popBackStack()
            } else {
                val errorMessage = getErrorMessageForEmailSending(task.exception)

                mainViewModel.setSnackBarMsg(errorMessage)
            }
        }
    }
    private fun getErrorMessageForEmailSending(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "등록되지 않은 이메일입니다."
            is FirebaseAuthInvalidCredentialsException -> "이메일 형식이 올바르지 않습니다."
            else -> "오류가 발생했습니다.나중에 다시 시도해 주세요."
        }
    }

    fun TextView.showError(msg: String) {
        val animShake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake_error)

        text = msg
        startAnimation(animShake)
    }

    private fun goBackToLogIn() {
        binding.btnGoToLogIn.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun hideKeyBoard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
}