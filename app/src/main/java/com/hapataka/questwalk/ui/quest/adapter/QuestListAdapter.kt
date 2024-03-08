package com.hapataka.questwalk.ui.quest.adapter

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

class QuestListAdapter(
    val onClickMoreText: (QuestData, Long) -> Unit,
    val onClickView: (String) -> Unit
) : ListAdapter<QuestData, QuestListAdapter.QuestViewHolder>(diffUtil) {
    private var allUser = 1L
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestViewHolder(ItemQuestBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestViewHolder(private val binding: ItemQuestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val imageList = listOf(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3,
            binding.ivImage4
        )

        fun bind(item: QuestData) {
            if (allUser == 0L) {
                binding.tvSolvePercent.text = "로딩중"
            }
            val completeRate = if (item.successItems.size > 0) {
                round((item.successItems.size.toDouble() / allUser) * 100)
            }  else {
                0.0
            }

            imageList.forEach { it.load(R.drawable.image_empty) }

            binding.tvKeyword.text = item.keyWord
            binding.tvSolvePercent.text = "해결 인원$completeRate%"


            item.successItems.reversed().take(4).forEachIndexed { index, successItem ->
                imageList[index].load(successItem.imageUrl) {
                    crossfade(true)
                }
            }

            binding.tvMore.setOnClickListener {
                onClickMoreText(item, allUser)
            }

            binding.constraintQuest.setOnClickListener {
                onClickView(item.keyWord)
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

    fun setAllUser(allUser: Long) {
        this.allUser = allUser
    }

    private fun setImageView(questCnt: Int, imageList: List<ImageView>) {
        for (i in imageList.indices) {
            if (i < questCnt) imageList[i].visibility = View.VISIBLE
            else imageList[i].visibility = View.INVISIBLE
        }
    }


}