package com.hapataka.questwalk.ui.myinfo.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogEditNicknameBinding
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.util.ViewModelFactory

class NickNameChangeDialog (val prevName: String) : DialogFragment() {
    private var _binding: DialogEditNicknameBinding? = null
    private val binding get() = _binding!!
    var onNicknameChanged: ((newNickname: String) -> Unit)? = null
    private val mainViewModel: MainViewModel by activityViewModels { ViewModelFactory(requireContext()) }


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
        setSize()
        binding.etChangeNickname.setText(prevName)
        binding.btnChange.setOnClickListener {
            val newNickName = binding.etChangeNickname.text.toString().trim()

            if (newNickName == prevName) {
                view?.let { mainViewModel.setSnackBarMsg("변경된 정보가 없습니다.") }
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

    private fun setSize() {
        val displayMetrics = DisplayMetrics().also {
            (requireActivity().windowManager.defaultDisplay).getMetrics(it)
        }
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.85).toInt()
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }
}


