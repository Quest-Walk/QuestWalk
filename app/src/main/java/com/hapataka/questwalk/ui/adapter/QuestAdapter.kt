package com.hapataka.questwalk.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.resource.Achievement
import com.hapataka.questwalk.databinding.ItemAchieveBinding
import com.hapataka.questwalk.databinding.ItemQuestBinding
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.ui.TAG

class QuestAdapter(val context: Context) :
    ListAdapter<QuestStackEntity, QuestAdapter.QuestViewHolder>(diffUtil) {
    var onClick: QuestItemClick? = null

    interface QuestItemClick {
        fun onClick(item: QuestStackEntity)
    }

    inner class QuestViewHolder(private val binding: ItemQuestBinding) : ViewHolder(binding.root) {
        fun bind(item: QuestStackEntity) {
            binding.tvQuestTitle.text = item.keyWord
            binding.tvQuestLevel.text = item.level.toString()

            val images = item.successItems

            when (images.size) {
                0 -> {
                    binding.ivThumbnail1.load(R.drawable.no_image)
                    binding.ivThumbnail2.load(R.drawable.no_image)
                    binding.ivThumbnail3.load(R.drawable.no_image)
                    binding.ivThumbnail4.load(R.drawable.no_image)
                }

                1 -> {
                    binding.ivThumbnail1.load(images[0].imageUrl)
                    binding.ivThumbnail2.load(R.drawable.no_image)
                    binding.ivThumbnail3.load(R.drawable.no_image)
                    binding.ivThumbnail4.load(R.drawable.no_image)
                }

                2 -> {
                    binding.ivThumbnail1.load(images[0].imageUrl)
                    binding.ivThumbnail2.load(images[1].imageUrl)
                    binding.ivThumbnail3.load(R.drawable.no_image)
                    binding.ivThumbnail4.load(R.drawable.no_image)
                }

                3 -> {
                    binding.ivThumbnail1.load(images[0].imageUrl)
                    binding.ivThumbnail2.load(images[1].imageUrl)
                    binding.ivThumbnail3.load(images[2].imageUrl)
                    binding.ivThumbnail4.load(R.drawable.no_image)
                }

                else -> {
                    binding.ivThumbnail1.load(images[0].imageUrl)
                    binding.ivThumbnail2.load(images[1].imageUrl)
                    binding.ivThumbnail3.load(images[2].imageUrl)
                    binding.ivThumbnail4.load(images[3].imageUrl)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBind = ItemQuestBinding.inflate(inflater, parent, false)

        return QuestViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick?.onClick(item)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<QuestStackEntity>() {
            override fun areItemsTheSame(
                oldItem: QuestStackEntity,
                newItem: QuestStackEntity
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: QuestStackEntity,
                newItem: QuestStackEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}