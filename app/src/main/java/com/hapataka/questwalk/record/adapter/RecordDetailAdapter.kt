package com.hapataka.questwalk.record.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hapataka.questwalk.databinding.ItemRecordAchievementBinding
import com.hapataka.questwalk.databinding.ItemRecordHeaderBinding
import com.hapataka.questwalk.databinding.ItemRecordResultBinding
import com.hapataka.questwalk.record.model.RecordItem
import com.hapataka.questwalk.record.model.RecordItem.*

class RecordDetailAdapter(val context: Context) : ListAdapter<RecordItem, ViewHolder>(diffUtil) {
    private val HEADER_TYPE = 0
    private val RESULT_TYPE = 1
    private val ACHIEVEMENT_TYPE = 2

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
            thumbnail.
        }
    }

    inner class AchievementViewHolder(binding: ItemRecordAchievementBinding) :
        ViewHolder(binding.root) {
        val icon = binding.ivIconImage
        val name = binding.tvName
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
        TODO("Not yet implemented")
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