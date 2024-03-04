package com.hapataka.questwalk.ui.quest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.databinding.ItemQuestBinding
import com.hapataka.questwalk.ui.quest.QuestData
import kotlin.math.min

class QuestAdapter(
    val onClick: (QuestData) -> Unit
) : ListAdapter<QuestData, QuestAdapter.QuestViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestViewHolder(ItemQuestBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestViewHolder(private val binding: ItemQuestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val imageList = listOf<ImageView>(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3,
            binding.ivImage4
        )
        fun bind(item: QuestData) {
            setImageView(item.successItems.size, imageList)
            binding.tvKeyword.text = item.keyWord
            item.successItems.take(4).forEachIndexed { index, successItem ->
                imageList[index].load(successItem.imageUrl)
            }

            binding.tvMore.setOnClickListener {
                onClick(item)
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

    private fun setImageView(questCnt: Int, imageList: List<ImageView>) {
        for (i in imageList.indices) {
            if (i < questCnt) imageList[i].visibility = View.VISIBLE
            else imageList[i].visibility = View.INVISIBLE
        }
    }


}