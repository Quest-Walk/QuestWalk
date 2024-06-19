package com.hapataka.questwalk.ui.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.facade.AuthFacade
import com.hapataka.questwalk.util.extentions.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val VALIDATE_SUCCESS = 0
const val EMPTY_INPUT = 1
const val NOT_EMAIL_TYPE = 2
const val CONFIRM_PW_EMPTY = 2
const val SHORT_PW = 3
const val PW_NOT_MATCH = 4

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authFacade: AuthFacade
) : ViewModel() {
    private var _isValidId = MutableLiveData<Int>()
    val isValidId: MutableLiveData<Int> get() = _isValidId

    private var _isValidPw = MutableLiveData<Int>()
    val isValidPw: MutableLiveData<Int> get() = _isValidPw

    private var _isRegisterSuccess = MutableLiveData<Boolean>()
    val isRegisterSuccess: MutableLiveData<Boolean> get() = _isRegisterSuccess

    private var _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> get() = _toastMsg

    private var _signUpBtnState = MutableLiveData<Boolean>(false)
    val signUpBtnState: LiveData<Boolean> get() = _signUpBtnState

    fun validateId(id: String) {
        when {
            id.isEmpty() -> _isValidId.value = EMPTY_INPUT
            Patterns.EMAIL_ADDRESS.matcher(id).matches().not() -> _isValidId.value = NOT_EMAIL_TYPE
            else -> _isValidId.value = VALIDATE_SUCCESS
        }
    }

    fun validatePw(pw: String, confirmPw: String) {
        when {
            pw.isEmpty() -> _isValidPw.value = EMPTY_INPUT
            pw.length < 6 -> _isValidPw.value = SHORT_PW
            confirmPw.isEmpty() -> _isValidPw.value = CONFIRM_PW_EMPTY
            pw != confirmPw -> _isValidPw.value = PW_NOT_MATCH
            else -> _isValidPw.value = VALIDATE_SUCCESS
        }
    }

    fun registerByIdAndPw(id: String, pw: String, confirmPw: String) {
        _signUpBtnState.value = false

        if (isValidId.value != VALIDATE_SUCCESS) {
            validateId(id)
            _signUpBtnState.value = true
            return
        }

        if (isValidPw.value != VALIDATE_SUCCESS) {
            validatePw(pw, confirmPw)
            _signUpBtnState.value = true
            return
        }

        viewModelScope.launch {
            val result = authFacade.registerByIdAndPw(id, pw)

            if (result.isSuccess) {
                _isRegisterSuccess.value = true
                delay(2000L)
                _signUpBtnState.value = true
            } else {
                val exception = result.exceptionOrNull() ?: return@launch

                _isRegisterSuccess.value = false
                _toastMsg.value = exception.getErrorMessage()
                _signUpBtnState.value = true
            }
        }
    }
}
