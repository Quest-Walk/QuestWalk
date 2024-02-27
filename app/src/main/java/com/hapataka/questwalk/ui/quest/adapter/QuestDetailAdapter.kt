package com.hapataka.questwalk.ui.quest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.databinding.ItemQuestBinding
import com.hapataka.questwalk.databinding.ItemQuestDetailBinding
import com.hapataka.questwalk.ui.quest.QuestStatsEntity

class QuestDetailAdapter: ListAdapter<String, QuestDetailAdapter.QuestDetailViewHolder>(diffUtil) {

    interface OnImageViewClickListener {
        fun clickImageView(email: String, url: Int)
    }
    var listener: OnImageViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestDetailViewHolder(ItemQuestDetailBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestDetailViewHolder(private val binding: ItemQuestDetailBinding): RecyclerView.ViewHolder(binding.root) {
        private val successItemsList = mutableListOf<Map.Entry<String, Int>>()
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