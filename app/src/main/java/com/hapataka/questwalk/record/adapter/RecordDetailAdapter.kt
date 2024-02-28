package com.hapataka.questwalk.record.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.hapataka.questwalk.databinding.ItemRecordAchievementBinding
import com.hapataka.questwalk.databinding.ItemRecordHeaderBinding
import com.hapataka.questwalk.databinding.ItemRecordResultBinding
import com.hapataka.questwalk.record.model.RecordItem
import com.hapataka.questwalk.record.model.RecordItem.*

const val HEADER_TYPE = 0
const val RESULT_TYPE = 1
const val ACHIEVEMENT_TYPE = 2

class RecordDetailAdapter(val context: Context) : ListAdapter<RecordItem, ViewHolder>(diffUtil) {
    inner class HeaderViewHolder(binding: ItemRecordHeaderBinding) : ViewHolder(binding.root) {
        private val title = binding.tvTitle

        fun bind(item: Header) {
            title.text = item.title
        }
    }

    inner class ResultViewHolder(binding: ItemRecordResultBinding) : ViewHolder(binding.root) {
        val thumbnail = binding.ivThumbnail
        val isSuccess = binding.ivSuccess

        fun bind(item: Result) {
            thumbnail.load(item.thumbnail)
        }
    }

    inner class AchievementViewHolder(binding: ItemRecordAchievementBinding) :
        ViewHolder(binding.root) {
        val icon = binding.ivIconImage
        val name = binding.tvName

        fun bind(item: Achievement) {
            icon.load(item.icon)
            name.text = item.name
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        return when (item) {
            is Header -> HEADER_TYPE
            is Result -> RESULT_TYPE
            is Achievement -> ACHIEVEMENT_TYPE
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val headerBind = ItemRecordHeaderBinding.inflate(inflater, parent, false)
        val resultBind = ItemRecordResultBinding.inflate(inflater, parent, false)
        val achievementBind = ItemRecordAchievementBinding.inflate(inflater, parent, false)

        return when(viewType) {
            HEADER_TYPE -> HeaderViewHolder(headerBind)
            RESULT_TYPE -> ResultViewHolder(resultBind)
            ACHIEVEMENT_TYPE -> AchievementViewHolder(achievementBind)
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        when(holder) {
            is HeaderViewHolder -> holder.bind(item as Header)
            is ResultViewHolder -> holder.bind(item as Result)
            is AchievementViewHolder -> holder.bind(item as Achievement)
        }
    }

    companion object {
        val diffUtil = object : ItemCallback<RecordItem>() {
            override fun areItemsTheSame(oldItem: RecordItem, newItem: RecordItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: RecordItem, newItem: RecordItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}