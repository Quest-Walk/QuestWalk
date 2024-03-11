package com.bongpal.adminapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bongpal.adminapp.databinding.FragmentQuestAddBinding


class QuestAddFragment : Fragment() {
    private val binding by lazy { FragmentQuestAddBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_add, container, false)
    }
}