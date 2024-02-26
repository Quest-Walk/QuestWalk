package com.hapataka.questwalk.homefragment

import android.content.res.ColorStateList
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {


    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    //모험 대기(wait),모험 시작(play)
    private var isPlay = false

    //타이머

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
                if (!isPlay) { // 모험 시작!
                    clPlayingBottomWidgets.visibility = View.VISIBLE
                    clPlayingUpperWidgets.visibility = View.VISIBLE
                    clWaitingBottomWidgets.visibility = View.GONE
                    isPlay = true
                    btnQuestStatus.text = "포기하기"
                    setBackgroundWidget(btnQuestStatus,R.color.red)
                } else { // 모험이 끝날때!
                    clPlayingBottomWidgets.visibility = View.GONE
                    clPlayingUpperWidgets.visibility = View.GONE
                    clWaitingBottomWidgets.visibility = View.VISIBLE
                    isPlay = false


                    // 버튼색,이름 바꾸기
                    btnQuestStatus.text = "모험 시작하기"
                    setBackgroundWidget(btnQuestStatus,R.color.green)
                }
            }
        }
    }


    // backgroundTint 값 변경
    private fun setBackgroundWidget(view : View,colorResource :Int){
        val colorStateList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                colorResource
            )
        )
        ViewCompat.setBackgroundTintList(view, colorStateList)
    }
}