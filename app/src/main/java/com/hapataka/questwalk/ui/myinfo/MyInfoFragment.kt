package com.hapataka.questwalk.ui.myinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentMyInfoBinding
import com.hapataka.questwalk.showSnackbar
import kotlinx.coroutines.launch

class MyInfoFragment : Fragment() {
    private val binding by lazy { FragmentMyInfoBinding.inflate(layoutInflater) }
    private val authRepo by lazy { AuthRepositoryImpl() }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        initLogoutButton()
        initDropOut()
    }

    private fun initLogoutButton() {
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                authRepo.logout()
                navController.navigate(R.id.action_frag_my_info_to_frag_login)
                "로그아웃 완료".showSnackbar(requireView())
            }
        }
    }

    private fun initDropOut() {
        binding.btnDropOut.setOnClickListener {
            lifecycleScope.launch {
                authRepo.deleteCurrentUser() {task ->
                    if (task.isSuccessful) {
                        navController.navigate(R.id.action_frag_my_info_to_frag_login)
                        "탈퇴완료".showSnackbar(requireView())
                    }
                }
            }
        }
    }
}