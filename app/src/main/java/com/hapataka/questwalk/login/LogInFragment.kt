package com.hapataka.questwalk.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.hapataka.questwalk.OnBoardingFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentLogInBinding


class LogInFragment : Fragment() {
    private var _binding : FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 100
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()


   //     initGoogleSignInClient()
        logIn()
        goToSignUp()
   //     logInWithGoogle()
   //     signUpWithKakao()


    }


    private fun logIn() {
        binding.btnLogIn.setOnClickListener {
            val id = binding.etLogInId.text.toString()
            val passWord = binding.etLogInPassWord.text.toString()

            if (id.isNotEmpty() && passWord.isNotEmpty()) {
                loginUser(id,passWord)
            } else Snackbar.make(requireView(),"이메일 또는 비밀번호가 비어있습니다", Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()){ task ->
                if (!task.isSuccessful) {
                    val exception = task.exception
                    if (exception is FirebaseAuthException){
                        val errorCode = exception.errorCode
                        val errorMessage = when (errorCode){
                            "ERROR_INVALID_EMAIL" -> "이메일 주소가 유효하지 않습니다."
                            "ERROR_USER_NOT_FOUND" -> "계정을 찾을 수 없습니다. 가입되지 않은 이메일입니다."
                            "ERROR_WRONG_PASSWORD" -> "비밀번호가 틀렸습니다. 다시 확인해주세요."
                            "ERROR_USER_DISABLED" -> "이 계정은 비활성화되었습니다. 관리자에게 문의해주세요."
                            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "이미 다른 인증 방법으로 등록된 이메일입니다."
                            "ERROR_EMAIL_ALREADY_IN_USE" -> "이 이메일은 이미 사용 중입니다. 다른 이메일을 사용해주세요."
                            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "이 인증 정보는 이미 다른 계정에서 사용 중입니다."
                            "ERROR_OPERATION_NOT_ALLOWED" -> "이메일 및 비밀번호 로그인이 활성화되지 않았습니다."
                            "ERROR_TOO_MANY_REQUESTS" -> "요청이 너무 많습니다. 나중에 다시 시도해주세요."
                            else -> "로그인 실패: 알 수 없는 오류가 발생했습니다. 다시 시도해주세요."
                        }
                        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(requireView(), "로그인 정보를 다시 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(requireView(), "로그인 되었습니다.", Snackbar.LENGTH_SHORT).show()
                    switchFragment(requireFragmentManager(), OnBoardingFragment(), false)
                }
            }
    }

//    private fun initGoogleSignInClient() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//    }

//    private fun logInWithGoogle(){
//        binding.ivGoogle.setOnClickListener {
//            val signInIntent = googleSignInClient.signInIntent
//            startActivityForResult(signInIntent,RC_SIGN_IN)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(requireView(), "Google 로그인 실패", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(requireView(), "로그인 응답 없음", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Snackbar.make(requireView(), "Google 로그인 성공", Snackbar.LENGTH_SHORT).show()
                switchFragment(requireFragmentManager(), OnBoardingFragment(), false)
            } else {
                Snackbar.make(requireView(), "Firebase 인증 실패", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun goToSignUp() {
        binding.tvGoJoin.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_frag_sign_up)
        }
    }

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



    //Fragment 전환
    fun switchFragment(fragmentManager: FragmentManager, fragment: Fragment, addToBackStack: Boolean) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.btn_logIn, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}