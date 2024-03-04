package com.hapataka.questwalk.ui.quest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.databinding.ItemQuestDetailBinding

class QuestDetailAdapter(
    private val onClick: (url: String) -> Unit
): ListAdapter<String, QuestDetailAdapter.QuestDetailViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestDetailViewHolder(ItemQuestDetailBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestDetailViewHolder(private val binding: ItemQuestDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.ivQuest.load(item)
        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }


}