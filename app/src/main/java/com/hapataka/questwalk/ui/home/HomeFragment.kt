package com.hapataka.questwalk.ui.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.camerafragment.CameraFragment
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {


    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private var backPressedOnce = false

    //모험 대기(wait),모험 시작(play)
    private var isPlay = false

    //타이머
    private lateinit var timer : Chronometer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        timer = binding.cmQuestTime
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        // TODO: Use the ViewModel
        initView()
    }

    private fun initView() {
        replaceFragmentByImageButton()
        setQuestButtonEvent()
        initBackPressedCallback()
    }

    /**
     *  프래그먼트 간의 이동
     */

    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private fun replaceFragmentByImageButton(){
        with(binding){
            ibHistory.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_record)
            }
            ibMyPage.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_my_info)
            }
            ibWheather.setOnClickListener {
                // TODO : wheatherFragment 이동
            }
            ibCamera.setOnClickListener {
//                navController.navigate(R.id.action_frag_home_to_frag_camera)
                replaceFragment(CameraFragment())
            }
            btnQuestChange.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_quest)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_graph,fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setQuestButtonEvent(){
        with(binding) {
            btnQuestStatus.setOnClickListener {
                if (!isPlay) { // 모험 시작!
                    clPlayingBottomWidgets.visibility = View.VISIBLE
                    clPlayingUpperWidgets.visibility = View.VISIBLE
                    clWaitingBottomWidgets.visibility = View.GONE
                    isPlay = true

                    //버튼색 이름 및 색깔 변경
                    btnQuestStatus.text = "포기하기"
                    setBackgroundWidget(btnQuestStatus,R.color.red)

                    //타이머
                    timer.base = SystemClock.elapsedRealtime()
                    timer.start()
                } else { // 모험이 끝날때!
                    clPlayingBottomWidgets.visibility = View.GONE
                    clPlayingUpperWidgets.visibility = View.GONE
                    clWaitingBottomWidgets.visibility = View.VISIBLE
                    isPlay = false


                    //버튼색 이름 및 색깔 변경
                    btnQuestStatus.text = "모험 시작하기"
                    setBackgroundWidget(btnQuestStatus,R.color.green)

                    //타이머 중지
                    timer.stop()
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

    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish()
                    return
                }
                backPressedOnce = true
                Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(2000)
                    backPressedOnce = false
                }

            }
        }.also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }
}