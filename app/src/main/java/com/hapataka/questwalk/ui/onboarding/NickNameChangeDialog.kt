package com.hapataka.questwalk.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.databinding.DialogEditNicknameBinding
import com.hapataka.questwalk.ui.login.showSnackbar

class NickNameChangeDialog : DialogFragment() {
    private var _binding: DialogEditNicknameBinding? = null
    private val binding get() = _binding!!
    var onNicknameChanged: ((newNickname: String) -> Unit)? = null

    override fun onStart() {
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogEditNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialog()
        closeDialog()
    }

    private fun setDialog() {
        binding.btnChange.setOnClickListener {
            val newNickName = binding.etChangeNickname.text.toString().trim()
            if (newNickName.isBlank()) {
                view?.let { "변경된 정보가 없습니다.".showSnackbar(it) }
                dismiss()
            } else {
                onNicknameChanged?.invoke(newNickName)
                dismiss()
            }
        }
    }


    private fun closeDialog() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


