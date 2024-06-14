package com.hapataka.questwalk.ui.myinfo.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogDropOutInputPwBinding

class InputPwDialog : DialogFragment() {
    private var _binding: DialogDropOutInputPwBinding? = null
    private val binding get() = _binding!!
    var onInputPw: ((pw: String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogDropOutInputPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSize()
        initButton()
    }

    private fun initButton() {
        with(binding) {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                var pw = binding.etInputPw.text.toString()

                onInputPw?.invoke(pw)
            }
        }
    }

    private fun setSize() {
        var dialogWidth: Int
        val dialogHeight: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics

            dialogWidth = (windowMetrics.bounds.width() * 0.85).toInt()
            dialogHeight = (windowMetrics.bounds.height() * 0.3).toInt()
        } else {
            @Suppress("DEPRECATION")
            val displayMetrics = DisplayMetrics().also {
                requireActivity().windowManager.defaultDisplay.getRealMetrics(it)
            }

            dialogWidth = (displayMetrics.widthPixels * 0.85).toInt()
            dialogHeight = (displayMetrics.heightPixels * 0.3).toInt()
        }
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}