package com.hapataka.questwalk

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentOnBoardingBinding
import com.hapataka.questwalk.ui.login.showSnackbar
import kotlinx.coroutines.launch


class OnBoardingFragment : Fragment() {
    private var _binding : FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val userRepo by lazy { UserRepositoryImpl() }
    private val authRepo by lazy { AuthRepositoryImpl() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        changeProfile()
        goMain()
    }


    private fun changeProfile(){
        binding.ivProfileImage.setOnClickListener {

        }
    }



    private fun goMain() {
        binding.btnComplete.setOnClickListener {
            val nickName = binding.etNickname.text.toString()

//            if (nickName.isNotEmpty()) {
//                lifecycleScope.launch {
//                    val userId = authRepo.getCurrentUserUid()
//                    userRepo.setNickName(userId,nickName)
//                }

//                sharedPref = requireActivity().getSharedPreferences("isSeeOnBoarding", Context.MODE_PRIVATE)
//                sharedPref.edit().apply{
//                    putBoolean("isSeeOnBoarding",true)
//                    apply()
//                }

                val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

                navController.navigate(R.id.action_frag_on_boarding_to_frag_home)
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
//            } else {
//                ("닉네임을 입력해 주세요").showSnackbar(requireView())
//                return@setOnClickListener
//            }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}