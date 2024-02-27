package com.hapataka.questwalk.ui.quest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.databinding.ItemQuestBinding
import com.hapataka.questwalk.ui.quest.QuestStatsEntity

class QuestAdapter: ListAdapter<QuestStatsEntity, QuestAdapter.QuestViewHolder>(diffUtil) {

    interface OnImageViewClickListener {
        fun clickImageView(email: String, url: Int)
    }

    var listener: OnImageViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestViewHolder(ItemQuestBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestViewHolder(private val binding: ItemQuestBinding): RecyclerView.ViewHolder(binding.root) {
        private val imageList = listOf<ImageView>(binding.ivImage1, binding.ivImage2, binding.ivImage3, binding.ivImage4)
        private val successItemsList = mutableListOf<Map.Entry<String, Int>>()
        fun bind(item: QuestStatsEntity) {
            binding.tvKeyword.text = item.keyWord
            item.successItems.entries.take(4).forEachIndexed { index, entry ->
                successItemsList.add(entry)
                imageList[index].load(entry.value)
            }
            imageList.forEachIndexed { index, imageView ->
                imageView.setOnClickListener {
                    listener?.clickImageView(successItemsList[index].key, successItemsList[index].value)
                }
            }
        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<QuestStatsEntity>() {
            override fun areItemsTheSame(oldItem: QuestStatsEntity, newItem: QuestStatsEntity): Boolean {
                return oldItem.keyWord == newItem.keyWord
            }

            override fun areContentsTheSame(oldItem: QuestStatsEntity, newItem: QuestStatsEntity): Boolean {
                return oldItem == newItem
            }
        }
    }


}