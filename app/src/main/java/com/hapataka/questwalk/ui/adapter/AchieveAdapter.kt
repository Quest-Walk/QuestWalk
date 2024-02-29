package com.hapataka.questwalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.hapataka.questwalk.data.resource.Achievement
import com.hapataka.questwalk.databinding.ItemAchieveBinding
import com.hapataka.questwalk.ui.adapter.AchieveAdapter.*

class AchieveAdapter(val context: Context) : ListAdapter<Achievement, AchieveViewHolder>(diffUtil) {
    var onClick: ItemClick? = null
    interface ItemClick {
        fun onClick(item: Achievement)
    }

    inner class AchieveViewHolder(private val binding: ItemAchieveBinding) : ViewHolder(binding.root) {
        fun bind(item: Achievement) {
            with(binding) {
                tvAchieveTitle.text = item.achieveTitle
                ivAchieveIcon.load(item.achieveIcon)
                tvDiscription.text = item.achieveDescription
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchieveViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBind = ItemAchieveBinding.inflate(inflater, parent, false)

        return AchieveViewHolder(itemBind)
    }

    override fun onBindViewHolder(holder: AchieveViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick?.onClick(item)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Achievement>() {
            override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
                return oldItem == newItem
            }
        }
    }
}