package com.hapataka.questwalk.ui.quest.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.ItemQuestBinding
import com.hapataka.questwalk.databinding.ItemQuestFooterBinding
import com.hapataka.questwalk.databinding.ItemQuestHeaderBinding
import com.hapataka.questwalk.ui.quest.QuestData
import kotlin.math.roundToInt

private const val HEADER = 0
private const val ITEM = 1
private const val FOOTER = 2
class QuestListAdapter(
    val context: Context,
    val onClickMoreText: (QuestData, Long) -> Unit,
    val onClickView: (String) -> Unit
) : ListAdapter<QuestData, RecyclerView.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            HEADER -> HeaderViewHolder(ItemQuestHeaderBinding.inflate(layoutInflater, parent, false))
            FOOTER -> FooterViewHolder(ItemQuestFooterBinding.inflate(layoutInflater, parent, false))
            else -> QuestViewHolder(ItemQuestBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder:  RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is QuestViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return HEADER
        }
        if (position == currentList.size - 1 ) {
            return FOOTER
        }
        return ITEM
    }

    inner class QuestViewHolder(private val binding: ItemQuestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestData) {
            val completeRate = if (item.allUser != 0L) {
                (((item.successItems.size.toDouble() / item.allUser)*100) *10.0 ).roundToInt() / 10.0
            } else {
                0.0
            }

            val leveImg = when (item.level) {
                1 -> {
                    R.drawable.ic_lv_01
                }
                2 -> {
                    R.drawable.ic_lv_02
                }
                else -> {
                    R.drawable.ic_lv_03
                }
            }

            with(binding) {
                if (completeRate > 50) {
                    tvSolveUnder.visibility = View.VISIBLE
                    tvSolveUnder.text = "$completeRate % 달성"
                } else {
                    tvSolveOver.visibility = View.VISIBLE
                    tvSolveOver.text = "$completeRate % 달성"
                }
                tvKeyword.text = item.keyWord
                ivLevel.load(leveImg)

                binding.progress.setProgressPercentage(0.0, false)
                binding.progress.setProgressPercentage(completeRate, true)

                btnMore.setOnClickListener {
                    if (item.successItems.isEmpty()) return@setOnClickListener
                    onClickMoreText(item, item.allUser)
                }

                binding.root.setOnClickListener {
                    onClickView(item.keyWord)
                }
            }
        }
    }

    inner class HeaderViewHolder(binding: ItemQuestHeaderBinding): RecyclerView.ViewHolder(binding.root) {}

    inner class FooterViewHolder(binding: ItemQuestFooterBinding): RecyclerView.ViewHolder(binding.root) {}

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<QuestData>() {
            override fun areItemsTheSame(oldItem: QuestData, newItem: QuestData): Boolean {
                return oldItem.keyWord == newItem.keyWord
            }

            override fun areContentsTheSame(oldItem: QuestData, newItem: QuestData): Boolean {
                return oldItem == newItem
            }
        }
    }
}