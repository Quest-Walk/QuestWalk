package com.hapataka.questwalk.ui.record.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hapataka.questwalk.data.resource.Achievements
import com.hapataka.questwalk.ui.record.RecordItemFragment
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.record.model.RecordItem.Header

class RecordItemAdapter(fragmentActivity: FragmentActivity, var items: List<RecordItem> = listOf()): FragmentStateAdapter(fragmentActivity) {
    private val page = 2

    override fun getItemCount(): Int = page

    override fun createFragment(position: Int): Fragment {
        val histories = listOf(Header("히스토리")) + items
        val achieveItems = Achievements.list.toList()
        var successAchieveIds = mutableListOf<Int>()

        items.filterIsInstance<AchieveItem>().forEach {
            successAchieveIds += it.achieveId
        }
        achieveItems.forEach {
            if (it is AchieveItem && successAchieveIds.contains(it.achieveId)) {
                it.isSuccess = true
            }
        }
        return when (position) {
            0 -> RecordItemFragment(histories)
            1 -> RecordItemFragment(achieveItems)
            else -> throw IllegalArgumentException("잘못된 페이지")
        }
    }
}