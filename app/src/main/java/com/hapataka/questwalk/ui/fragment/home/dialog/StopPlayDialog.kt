package com.hapataka.questwalk.ui.fragment.home.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogStopPlayBinding

class StopPlayDialog(private val distance: Float, val positiveCallback: () -> Unit, val negativeCallback: () -> Unit) : DialogFragment() {
    private val binding by lazy { DialogStopPlayBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogSize()
        initText()
        initButton()
        dialog?.setCancelable(false)
    }

    private fun setDialogSize() {
        val displayMetrics = DisplayMetrics().also {
            (requireActivity().windowManager.defaultDisplay).getMetrics(it)
        }
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.85).toInt()
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }

    private fun initText() {
        if (distance > 10f) {
            binding.tvTitle.text = "모험을 종료하고 지금까지의 기록을 저장할까요?"
        } else {
            binding.tvTitle.text = "지금 포기하면 이동한 거리가 너무 짧아서\n기록이 남지 않습니다.\n그래도 포기하시겠습니까?"
        }
    }

    private fun initButton() {
        with(binding) {
            btnCancel.setOnClickListener {
                negativeCallback()
                dismiss()
            }
            btnConfirm.setOnClickListener {
                positiveCallback()
                dismiss()
            }
        }
    }
}