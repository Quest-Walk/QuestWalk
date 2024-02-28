package com.hapataka.questwalk.SIGNUP

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel : ViewModel() {
    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean> = _signUpResult

    fun registerUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _signUpResult.value = false
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _signUpResult.value = task.isSuccessful
            }
    }
}
