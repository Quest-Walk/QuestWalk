package com.hapataka.questwalk.ui.fragment.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.LocalRepository
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val autoRepo: AuthRepository,
    private val localRepo: LocalRepository
) : ViewModel() {
    fun registerByEmailAndPw(
        email: String,
        pw: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            autoRepo.registerByEmailAndPw(email, pw) { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError()
                }
            }
        }
    }

    fun logByEmailAndPw(email: String, pw: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            autoRepo.loginByEmailAndPw(email, pw) { task ->
                if (task.isSuccessful) {
                    localRepo.setUserId(email)
                    onSuccess()
                } else {
                    onError()
                }
            }
        }
    }
}
