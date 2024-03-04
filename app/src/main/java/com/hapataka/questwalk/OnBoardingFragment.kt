package com.hapataka.questwalk

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.databinding.FragmentOnBoardingBinding
import com.hapataka.questwalk.domain.entity.UserEntity


class OnBoardingFragment : Fragment() {
    private var _binding : FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val userRepo by lazy { UserRepositoryImpl() }


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

            if (!nickName.isNullOrBlank()) {

                val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

                navController.navigate(R.id.action_frag_on_boarding_to_frag_home)
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
            }


        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}