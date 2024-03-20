package com.hapataka.questwalk.ui.quest.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.ItemQuestBinding
import com.hapataka.questwalk.ui.quest.QuestData
import kotlin.math.round
import kotlin.math.roundToInt

class QuestListAdapter(
    val onClickMoreText: (QuestData, Long) -> Unit,
    val onClickView: (String) -> Unit
) : ListAdapter<QuestData, QuestListAdapter.QuestViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestViewHolder(ItemQuestBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestViewHolder(private val binding: ItemQuestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuestData) {
            Log.d("QuestListAdapter:","${item.allUser}")
            Log.d("QuestListAdapter:","${item.successItems.size}")
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
                tvSolvePercent.text = "$completeRate %"
                tvKeyword.text = item.keyWord
                ivLevel.load(leveImg)

                ValueAnimator.ofInt(0, completeRate.toInt()).apply {
                    duration = 800
                    addUpdateListener { animation ->
                        progressBar.progress = animation.animatedValue as Int
                    }
                }.start()

                btnMore.setOnClickListener {
                    if (item.successItems.isEmpty()) return@setOnClickListener
                    onClickMoreText(item, item.allUser)
                }

                btnSelect.setOnClickListener {
                    onClickView(item.keyWord)
                }
            }
        }
    }

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