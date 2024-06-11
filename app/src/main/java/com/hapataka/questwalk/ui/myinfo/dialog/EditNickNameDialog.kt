package com.hapataka.questwalk.ui.myinfo.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogEditNicknameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNickNameDialog (private val prevName: String) : DialogFragment() {
    private var _binding: DialogEditNicknameBinding? = null
    private val binding get() = _binding!!

    var onNicknameChanged: ((newNickname: String) -> Result<Boolean>)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogEditNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialog()
        setButtons()
    }

    private fun setDialog() {
        setSize()
        binding.etChangeNickname.setText(prevName)
    }


    private fun setButtons() {
        binding.btnChange.setOnClickListener {
            val newNickName = binding.etChangeNickname.text.toString().trim()

            onNicknameChanged?.invoke(newNickName)?.let {
                if (it.isSuccess) dismiss()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSize() {
        var dialogWidth: Int
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics

            dialogWidth = (windowMetrics.bounds.width() * 0.85).toInt()
        } else {
            @Suppress("DEPRECATION")
            val displayMetrics = DisplayMetrics().also {
                requireActivity().windowManager.defaultDisplay.getRealMetrics(it)
            }

            dialogWidth = (displayMetrics.widthPixels * 0.85).toInt()
        }
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }
}


