package com.hapataka.questwalk.camerafragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.databinding.DialogCapturedImageBinding

class CapturedImageDialog: DialogFragment() {
    private val binding by lazy{ DialogCapturedImageBinding.inflate(layoutInflater)}
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // 다이얼 로그 화면 등록
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)

        return dialog
    }


}