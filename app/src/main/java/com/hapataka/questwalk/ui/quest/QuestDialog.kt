package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.databinding.DialogQuestBinding
import com.hapataka.questwalk.ui.home.HomeViewModel

class QuestDialog(
    private val keyWord: String,
    private val successKeywords: MutableList<String>,
    private val callback: (String) -> Unit
): DialogFragment() {
    private val binding by lazy { DialogQuestBinding.inflate(layoutInflater) }
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDeviceSize()
        initButtons()
        binding.tvKeyword.text = keyWord
    }

    private fun initButtons() {
        with(binding) {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                if (successKeywords.contains(keyWord)) {
                    callback("이미 완료한 키워드입니다 다른 키워드를 선택해주세요")
                } else {
                    homeViewModel.setKeyword(keyWord)
                    navHost.popBackStack()
                }
                dismiss()
            }
        }
    }

    private fun getDeviceSize() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog?.window?.apply {
            setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            isCancelable = false
        }
    }
}