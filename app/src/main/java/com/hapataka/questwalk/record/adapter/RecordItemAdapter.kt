package com.hapataka.questwalk.record.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hapataka.questwalk.record.RecordItemFragment

class RecordItemAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val page = 2

    override fun getItemCount(): Int = page

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecordItemFragment()
            1 -> RecordItemFragment()
            else -> throw IllegalArgumentException("잘못된 페이지")
        }
    }
}