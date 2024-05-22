package com.hapataka.questwalk.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hapataka.questwalk.databinding.DialogChooseCharacterBinding
import com.hapataka.questwalk.ui.onboarding.CharacterData.Companion.characterList


class ChooseCharacterDialog : DialogFragment() {
    private var _binding : DialogChooseCharacterBinding? = null
    var listener: OnCharacterSelectedListener? = null
    private val binding get() = _binding!!
    private lateinit var adapter : ChooseCharacterAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogChooseCharacterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        close()
    }

    private fun setRecyclerView() {
        adapter = ChooseCharacterAdapter(characterList) {characterData ->
            listener?.onCharacterSelected(characterData)
        dismiss()
        }

        val recyclerView = binding.rvChooseCharacter
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context,2)
    }

    private fun close() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

