package com.hapataka.questwalk.ui.record.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hapataka.questwalk.ui.record.RecordItemFragment
import com.hapataka.questwalk.ui.record.model.RecordItem

class RecordItemAdapter(fragmentActivity: FragmentActivity, var items: List<RecordItem> = listOf()): FragmentStateAdapter(fragmentActivity) {
    private val page = 2

    override fun getItemCount(): Int = page

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecordItemFragment(items)
            1 -> RecordItemFragment(items)
            else -> throw IllegalArgumentException("잘못된 페이지")
        }
    }
}