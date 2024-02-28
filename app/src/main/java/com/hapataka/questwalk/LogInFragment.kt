package com.hapataka.questwalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.hapataka.questwalk.SIGNUP.SignUpFragment
import com.hapataka.questwalk.databinding.FragmentLogInBinding


class LogInFragment : Fragment() {
    private var _binding : FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 100



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


  //      initGoogleSignInClient()
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
            } else Snackbar.make(requireView(),"이메일과 비밀번호가 비어있습니다", Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun loginUser(email : String , password : String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Snackbar.make(requireView(), "로그인 되었습니다.", Snackbar.LENGTH_SHORT).show()
                switchFragment(requireFragmentManager(),OnBoardingFragment(),false)
            } else {
                Snackbar.make(requireView(), "로그인 실패", Snackbar.LENGTH_SHORT).show()
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
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(requireView(),"Google로그인 실패", Snackbar.LENGTH_SHORT).show()
            }
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
            switchFragment(requireFragmentManager(), SignUpFragment(),false)
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

    private fun firebaseAuthWithKakao(accessToken: String) {
        val credential = OAuthProvider.newCredentialBuilder("oidc.kakao")
            .setAccessToken(accessToken)
            .build()

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    val user = task.result?.user
                    Snackbar.make(requireView(), "Firebase 로그인 성공: ${user?.uid}", Snackbar.LENGTH_SHORT).show()
                } else {
                    // 로그인 실패
                    Snackbar.make(requireView(), "Firebase 로그인 실패???  ", Snackbar.LENGTH_SHORT).show()
                    Log.e("로그디","Authentication failed",task.exception)
                }
            }
    }



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