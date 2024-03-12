package com.hapataka.questwalk.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.databinding.DialogHomeBinding

class HomeDialog(
    private val callback: () -> Unit
): DialogFragment() {
    private val binding by lazy { DialogHomeBinding.inflate(layoutInflater) }

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
    }

    private fun initButtons() {
        with(binding) {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                callback()
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