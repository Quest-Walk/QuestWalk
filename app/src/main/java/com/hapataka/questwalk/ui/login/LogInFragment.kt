package com.hapataka.questwalk.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthException
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentLogInBinding
import com.hapataka.questwalk.util.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LogInFragment : BaseFragment<FragmentLogInBinding>(FragmentLogInBinding::inflate) {
    private val RC_SIGN_IN = 100
    private val authRepo by lazy { AuthRepositoryImpl() }
    private val userRepo by lazy { UserRepositoryImpl() }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setup()
    }

    private fun initView() {
        initLoginButton()
        initSignUpButton()
        initFindPassWordButton()
    }

    private fun initFindPassWordButton() {
        binding.tvFindPassWord.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_findPassWordFragment)
        }
    }

    private fun setup() {
        initBackPressedCallback()
    }

    private fun initLoginButton() {
        with(binding) {
            btnLogin.setOnClickListener {
                val id = etLoginId.text.toString()
                val pw = etLoginPassword.text.toString()

                loginByEmailPassword(id, pw)
                hideKeyBoard()
            }
        }
    }

    private fun initSignUpButton() {
        binding.btnSignUp.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_frag_sign_up)
        }
    }

    private fun loginByEmailPassword(id: String, pw: String) {
        if (id.isEmpty() || pw.isEmpty()) {
            "이메일 또는 비밀번호가 비어있습니다".showSnackbar(requireView())
            return
        }

        lifecycleScope.launch {
            authRepo.loginByEmailAndPw(id, pw) { task ->
                if (task.isSuccessful) {
                    val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

                    navController.navigate(R.id.action_frag_login_to_frag_home)
                    navGraph.setStartDestination(R.id.frag_home)
                    navController.graph = navGraph
                    return@loginByEmailAndPw
                }
                val exception = task.exception

                if (exception is FirebaseAuthException) {
                    handleFirebaseAuthException(exception)
                } else ("로그인 정보를 다시 확인해 주세요.").showSnackbar(requireView())
            }
        }
    }

    private fun handleFirebaseAuthException(exception: FirebaseAuthException) {
        val errorCode = exception.errorCode
        Log.d("로그디", errorCode)
        val errorMessage = getErrorMessageByErrorCode(errorCode)
        errorMessage.showSnackbar(requireView())
    }

    private fun getErrorMessageByErrorCode(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "이메일 주소가 유효하지 않습니다."
            "ERROR_USER_NOT_FOUND" -> "계정을 찾을 수 없습니다. 가입되지 않은 이메일입니다."
            "ERROR_WRONG_PASSWORD" -> "비밀번호가 틀렸습니다. 다시 확인해주세요."
            "ERROR_USER_DISABLED" -> "이 계정은 비활성화되었습니다. 관리자에게 문의해주세요."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "이미 다른 인증 방법으로 등록된 이메일입니다."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "이 이메일은 이미 사용 중입니다. 다른 이메일을 사용해주세요."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "이 인증 정보는 이미 다른 계정에서 사용 중입니다."
            "ERROR_OPERATION_NOT_ALLOWED" -> "이메일 및 비밀번호 로그인이 활성화되지 않았습니다."
            "ERROR_TOO_MANY_REQUESTS" -> "요청이 너무 많습니다. 나중에 다시 시도해주세요."
            "ERROR_INVALID_CREDENTIAL" -> "아이디 또는 비밀번호가 유효하지 않습니다.\n 다시 시도해 주세요"
            else -> "로그인 실패: 알 수 없는 오류가 발생했습니다. 다시 시도해주세요."
        }
    }

    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish()
                    return
                }
                backPressedOnce = true
                Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }.also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }

    private fun hideKeyBoard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
}

fun String.showSnackbar(view: View) {
    Snackbar.make(view, this, Snackbar.LENGTH_SHORT).show()
}


// 실패코드 참고용입니당
//    private fun loginUser(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (!task.isSuccessful) {
//                    val exception = task.exception
//                    if (exception is FirebaseAuthException) {
//                        val errorCode = exception.errorCode
//                        val errorMessage = when (errorCode) {
//                            "ERROR_INVALID_EMAIL" -> "이메일 주소가 유효하지 않습니다."
//                            "ERROR_USER_NOT_FOUND" -> "계정을 찾을 수 없습니다. 가입되지 않은 이메일입니다."
//                            "ERROR_WRONG_PASSWORD" -> "비밀번호가 틀렸습니다. 다시 확인해주세요."
//                            "ERROR_USER_DISABLED" -> "이 계정은 비활성화되었습니다. 관리자에게 문의해주세요."
//                            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "이미 다른 인증 방법으로 등록된 이메일입니다."
//                            "ERROR_EMAIL_ALREADY_IN_USE" -> "이 이메일은 이미 사용 중입니다. 다른 이메일을 사용해주세요."
//                            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "이 인증 정보는 이미 다른 계정에서 사용 중입니다."
//                            "ERROR_OPERATION_NOT_ALLOWED" -> "이메일 및 비밀번호 로그인이 활성화되지 않았습니다."
//                            "ERROR_TOO_MANY_REQUESTS" -> "요청이 너무 많습니다. 나중에 다시 시도해주세요."
//                            else -> "로그인 실패: 알 수 없는 오류가 발생했습니다. 다시 시도해주세요."
//                        }
//                        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
//                    } else {
//                        Snackbar.make(requireView(), "로그인 정보를 다시 확인해 주세요.", Snackbar.LENGTH_SHORT)
//                            .show()
//                    }
//                } else {
//                    Snackbar.make(requireView(), "로그인 되었습니다.", Snackbar.LENGTH_SHORT).show()
//                    switchFragment(requireFragmentManager(), OnBoardingFragment(), false)
//                }
//            }
//    }


// 구글 로그인용 코드
//    private fun initGoogleSignInClient() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//    private fun logInWithGoogle(){
//        binding.ivGoogle.setOnClickListener {
//            val signInIntent = googleSignInClient.signInIntent
//            startActivityForResult(signInIntent,RC_SIGN_IN)
//        }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN && data != null) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)!!
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                Snackbar.make(requireView(), "Google 로그인 실패", Snackbar.LENGTH_SHORT).show()
//            }
//        } else {
//            Snackbar.make(requireView(), "로그인 응답 없음", Snackbar.LENGTH_SHORT).show()
//        }
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
//            if (task.isSuccessful) {
//                Snackbar.make(requireView(), "Google 로그인 성공", Snackbar.LENGTH_SHORT).show()
//                switchFragment(requireFragmentManager(), OnBoardingFragment(), false)
//            } else {
//                Snackbar.make(requireView(), "Firebase 인증 실패", Snackbar.LENGTH_SHORT).show()
//            }
//        }


//    private fun signUpWithKakao() {
//        binding.ivKakao.setOnClickListener {
//            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
//                UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
//                    if (error != null) {
//                        Snackbar.make(requireView(), "카카오 로그인 실패: $error", Snackbar.LENGTH_SHORT).show()
//                    } else if (token != null) {
//                        Snackbar.make(requireView(), "카카오 로그인 성공", Snackbar.LENGTH_SHORT).show()
//                        firebaseAuthWithKakao(token.accessToken)
//                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->  }
//                    }
//                }
//            } else {
//                UserApiClient.instance.loginWithKakaoAccount(requireContext()) { token, error ->
//                    if (error != null) {
//                        Snackbar.make(requireView(), "카카오 계정 로그인 실패: $error", Snackbar.LENGTH_SHORT).show()
//                    } else if (token != null) {
//                        Snackbar.make(requireView(), "카카오 계정 로그인 성공", Snackbar.LENGTH_SHORT).show()
//                        firebaseAuthWithKakao(token.accessToken)
//                    }
//                }
//            }
//        }
//    }

//
//    private fun firebaseAuthWithKakao(accessToken: String) {
//        val credential = OAuthProvider.newCredentialBuilder("oidc.kakao")
//            .setAccessToken(accessToken)
//            .build()
//
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // 로그인 성공
//                    val user = task.result?.user
//                    Snackbar.make(requireView(), "Firebase 로그인 성공: ${user?.uid}", Snackbar.LENGTH_SHORT).show()
//                } else {
//                    // 로그인 실패
//                    Snackbar.make(requireView(), "Firebase 로그인 실패???  ", Snackbar.LENGTH_SHORT).show()
//                    Log.e("로그디","Authentication failed",task.exception)
//                }
//            }
//    }

