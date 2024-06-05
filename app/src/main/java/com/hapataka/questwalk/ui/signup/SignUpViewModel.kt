package com.hapataka.questwalk.ui.signup

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.repository.AuthRepo
import com.hapataka.questwalk.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val VALIDATE_SUCCESS = 0
const val EMPTY_INPUT = 1
const val NOT_EMAIL_TYPE = 2

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val autoRepo: AuthRepo,
    private val localRepo: LocalRepository
) : ViewModel() {
    private var _isValidId = MutableLiveData<Int>()
    val isValidId: MutableLiveData<Int> get() = _isValidId

    fun validateId(id: String) {
        if (id.isEmpty()) {
            _isValidId.value = EMPTY_INPUT
            return
        }

        if (Patterns.EMAIL_ADDRESS.matcher(id).matches().not()) {
            _isValidId.value = NOT_EMAIL_TYPE
            return
        }
        _isValidId.value = VALIDATE_SUCCESS
    }

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
