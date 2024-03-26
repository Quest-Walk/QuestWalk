package com.hapataka.questwalk.ui.fragment.login

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentLogInBinding
import com.hapataka.questwalk.ui.activity.mainactivity.MainViewModel
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.OnSingleClickListener
import com.hapataka.questwalk.util.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LogInFragment : BaseFragment<FragmentLogInBinding>(FragmentLogInBinding::inflate) {
    private val authRepo by lazy { AuthRepositoryImpl() }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false
    private val mainViewModel: MainViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private val viewModel: LoginViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setup()
    }

    private fun initView() {
        initLoginButton()
        initSignUpButton()
        initFindPassWordButton()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun initFindPassWordButton() {
        binding.tvFindPassWord.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_findPassWordFragment)
        }
    }

    private fun setup() {
        initBackPressedCallback()
        setObserver()
    }

    private fun initLoginButton() {
        with(binding) {
            btnLogin.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    val id = etLoginId.text.toString()
                    val pw = etLoginPassword.text.toString()

                    viewModel.loginByEmailPassword(
                        id,
                        pw,
                        navigateCallback = { changeStartDestinationWithNavigate(id) },
                        snackBarMsg = { msg -> showSnackBar(msg) }
                    )
                    hideKeyBoard()
                }
            })
        }
    }

    private fun changeStartDestinationWithNavigate(id: String) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        viewModel.setUserId(id)
        navController.navigate(R.id.action_frag_login_to_frag_home)
        navGraph.setStartDestination(R.id.frag_home)
        navController.graph = navGraph
    }

    private fun initSignUpButton() {
        binding.btnSignUp.setOnClickListener {
            navController.navigate(R.id.action_frag_login_to_frag_sign_up)
        }
    }

    private fun showSnackBar(msg: String) {
        mainViewModel.setSnackBarMsg(msg)
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

    private fun setObserver() {
        viewModel.userId.observe(viewLifecycleOwner) { id ->
            binding.etLoginId.setText(id)
        }
    }
}