package com.example.firebasetestapp

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentFindPassWordBinding
import com.hapataka.questwalk.ui.login.showSnackbar


class FindPassWordFragment : Fragment() {

    private var _binding : FragmentFindPassWordBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var emailId = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindPassWordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSendEmailButton()
        goBackToLogIn()
    }

    private fun setUpSendEmailButton() {
        binding.btnSendEmail.setOnClickListener {
            sendEmail()
            hideKeyBoard()
        }
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
                "이메일을 보냈습니다. 메일함을 확인해 주세요.".showSnackbar(requireView())
            } else {
                val errorMessage = getErrorMessageForEmailSending(task.exception)
                errorMessage.showSnackbar(requireView())
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


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}