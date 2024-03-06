package com.hapataka.questwalk.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch

class SignUpViewModel(private val autoRepo : AuthRepositoryImpl) : ViewModel(){
    fun registerByEmailAndPw(email : String , pw : String, onSuccess : () -> Unit , onError : () -> Unit){
        viewModelScope.launch {
            autoRepo.registerByEmailAndPw(email , pw){ task ->
                if (task.isSuccessful){
                    onSuccess()
                }else{
                    onError()
                }
            }
        }

    }

    fun logByEmailAndPw(email: String , pw: String , onSuccess : () -> Unit , onError : () -> Unit){
        viewModelScope.launch {
            autoRepo.loginByEmailAndPw(email , pw){task ->
                if (task.isSuccessful){
                    onSuccess()
                }else{
                    onError()
                }
            }
        }
    }

}


class SignUpViewModelFactory(private val repo : AuthRepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel ::class.java)){
            return SignUpViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
