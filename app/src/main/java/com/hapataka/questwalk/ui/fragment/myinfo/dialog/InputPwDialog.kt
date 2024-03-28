package com.hapataka.questwalk.ui.fragment.myinfo.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogDropOutInputPwBinding

class InputPwDialog(val reauthCallback: (String) -> Unit, val snackBarCallback: (String) -> Unit) : DialogFragment() {
    private val binding by lazy { DialogDropOutInputPwBinding.inflate(layoutInflater) }

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
        initButton()
    }

    private fun setDialogSize() {
        val displayMetrics = DisplayMetrics().also {
            (requireActivity().windowManager.defaultDisplay).getMetrics(it)
        }
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val dialogWidth = (width * 0.85).toInt()
        val dialogHeight = (height * 0.3).toInt()

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }

    private fun initButton() {
        with(binding) {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                var pw = binding.etInputPw.text.toString()

                if (pw.isEmpty()) {
                    snackBarCallback("비밀번호를 입력해주세요.")
                    return@setOnClickListener
                }

                reauthCallback(pw)
            }
        }
    }
}