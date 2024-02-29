package com.hapataka.questwalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.databinding.FragmentSuccessBinding

class SuccessFragment: Fragment() {
    private var _binding : FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvBackToMenuBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}