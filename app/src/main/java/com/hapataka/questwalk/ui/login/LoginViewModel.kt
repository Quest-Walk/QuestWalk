package com.hapataka.questwalk.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.facade.AuthFacade
import com.hapataka.questwalk.util.extentions.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import javax.inject.Inject

const val TAG = "quest_walk_test_tag"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authFacade: AuthFacade,
) : ViewModel() {
    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private var _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private var _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> get() = _toastMsg

    private var _loginBtnState = MutableLiveData<Boolean>(true)
    val loginBtnState: LiveData<Boolean> get() = _loginBtnState

    fun loginByIdAndPw(id: String, pw: String) {
        Log.d(TAG, "로그인 눌려용")
        if (checkInput(id, pw).not()) {
            _loginBtnState.value = true
            return
        }
        viewModelScope.launch {
            _loginBtnState.value = false
            val result = authFacade.loginByIdAndPw(id, pw)

            if (result.isSuccess) {
                _loginResult.value = true
                delay(1000)
                _loginBtnState.value = true
            } else {
                val exception = result.exceptionOrNull() ?: return@launch

                _loginResult.value = false
                _toastMsg.value = exception.getErrorMessage()
                _loginBtnState.value = true
            }
        }
    }

    fun getIdFromPref() {
        viewModelScope.launch {
            authFacade.getUserIdFromPref()
                .collect { id -> if (id != null) _userId.value = id }
        }
    }

    private fun checkInput(id: String, pw: String): Boolean {
        if (id.isEmpty() && pw.isEmpty()) {
            _toastMsg.value = "아이디와 비밀번호를 입력해주세요."
            return false
        }

        if (id.isEmpty()) {
            _toastMsg.value = "아이디를 입력해주세요."
            return false
        }

        if (pw.isEmpty()) {
            _toastMsg.value = "비밀번호를 입력해주세요."
            return false
        }
        return true
    }
}