package com.hapataka.questwalk.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.facade.AuthFacade
import com.hapataka.questwalk.domain.facade.UserFacade
import com.hapataka.questwalk.util.extentions.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "quest_walk_test_tag"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authFacade: AuthFacade,
    private val userFacade: UserFacade,
) : ViewModel() {
    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private var _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private var _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> get() = _toastMsg

    private var _btnState = MutableLiveData<Boolean>(true)
    val btnState: LiveData<Boolean> get() = _btnState

    fun loginByIdAndPw(id: String, pw: String) {
        if (checkInput(id, pw).not()) return

        if (btnState.value == false) return

        viewModelScope.launch {
            _btnState.value = false
            val result = authFacade.loginByIdAndPw(id, pw)

            if (result.isSuccess) {
                _loginResult.value = true
            } else {
                val exception = result.exceptionOrNull() ?: return@launch

                _loginResult.value = false
                _toastMsg.value = exception.getErrorMessage()
            }
            _btnState.value = true
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