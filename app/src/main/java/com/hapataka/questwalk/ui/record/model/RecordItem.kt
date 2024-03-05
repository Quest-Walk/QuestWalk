package com.hapataka.questwalk.ui.record.model

sealed class RecordItem {
    data class Header(
        val title: String
    ) : RecordItem()

    data class ResultItem(
        val thumbnail: String?,
        val isSuccess: Boolean
    ) : RecordItem()

    data class AchieveItem(
        val achieveId: Int,
        val achieveIcon: Int,
        val achieveTitle: String,
        val achieveDescription: String,
        val isHidden: Boolean = false
    ) : RecordItem()
}
