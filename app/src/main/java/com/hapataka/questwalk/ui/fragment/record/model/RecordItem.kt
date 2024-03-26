package com.hapataka.questwalk.ui.fragment.record.model

sealed class RecordItem {
    data class Header(
        val title: String
    ) : RecordItem()

    data class ResultItem(
        val keyword: String,
        val thumbnail: String?,
        val isSuccess: Boolean,
        val registerAt: String
    ) : RecordItem()

    data class AchieveItem(
        val achieveId: Int,
        val achieveIcon: String,
        val achieveTitle: String,
        val achieveDescription: String,
        var isSuccess: Boolean = false,
        var isHidden: Boolean = false
    ) : RecordItem()
}
