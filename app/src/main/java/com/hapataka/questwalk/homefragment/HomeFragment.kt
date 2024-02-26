package com.hapataka.questwalk.homefragment

import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {


    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    //모험 대기(wait),모험 시작(play)
    private var isPlay = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        // TODO: Use the ViewModel
        initView()
    }

    private fun initView() {
        with(binding) {
            btnQuestStatus.setOnClickListener {
                if (!isPlay) {
                    clPlayingBottomWidgets.visibility = View.VISIBLE
                    clPlayingUpperWidgets.visibility = View.VISIBLE
                    clWaitingBottomWidgets.visibility = View.GONE
                    isPlay = true
                } else {
                    clPlayingBottomWidgets.visibility = View.GONE
                    clPlayingUpperWidgets.visibility = View.GONE
                    clWaitingBottomWidgets.visibility = View.VISIBLE
                    isPlay = false
                }
            }
        }
    }
}