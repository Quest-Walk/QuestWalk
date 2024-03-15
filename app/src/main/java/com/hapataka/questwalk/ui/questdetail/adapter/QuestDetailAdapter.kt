package com.hapataka.questwalk.ui.questdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hapataka.questwalk.databinding.ItemQuestDetailBinding
import com.hapataka.questwalk.ui.quest.QuestData

class QuestDetailAdapter(
    val itemClick: (uri:String, imageView: View) -> Unit
): ListAdapter<QuestData.SuccessItem, QuestDetailAdapter.QuestDetailViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return QuestDetailViewHolder(ItemQuestDetailBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestDetailViewHolder(private val binding: ItemQuestDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestData.SuccessItem) {
            with(binding) {
                ivQuest.load(item.imageUrl)
                binding.root.setOnClickListener {
                    itemClick(item.imageUrl, ivQuest)
                }
            }
        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<QuestData.SuccessItem>() {
            override fun areItemsTheSame(oldItem: QuestData.SuccessItem, newItem: QuestData.SuccessItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: QuestData.SuccessItem, newItem: QuestData.SuccessItem): Boolean {
                return oldItem == newItem
            }
        }
    }


}