package com.hapataka.questwalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {
    private var _binding : FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!


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
        changeNickName()
 //       goMain()
    }


    private fun changeProfile(){
        binding.ivProfileImage.setOnClickListener {

        }
    }

    private fun changeNickName() {
        binding.etNickName.setOnClickListener {

        }
    }

//    private fun goMain() {
//        binding.btnComplete.setOnClickListener {
//            val nickName = binding.etNickName.text.toString()
//
//            val database = FirebaseDatabase.getInstance()
//            val userRef = database.getReference("user")
//
//            val userId = userRef.push().key ?: return@setOnClickListener
//
//            val userNickName = UserData(nickName)
//
//
//            userRef.child(userId).setValue(userNickName)
//                .addOnSuccessListener {
//                    Log.d("로그디","유저 닉네임 등록 성공")
//                }
//                .addOnFailureListener {
//                    Log.d("로그디","유저 닉네임 등록 실패")
//                }
//        }
//    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}