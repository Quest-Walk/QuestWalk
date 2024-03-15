package com.hapataka.questwalk.ui.record.model

sealed class RecordItem {
    data class Header(
        val title: String
    ) : RecordItem()

    data class ResultItem(
        val keyword: String,
        val thumbnail: String?,
        val isFail: Boolean,
        val registerAt: String
    ) : RecordItem()

    data class AchieveItem(
        val achieveId: Int,
        val achieveIcon: Int,
        val achieveTitle: String,
        val achieveDescription: String,
        var isSuccess: Boolean = false,
        var isHidden: Boolean = false
    ) : RecordItem()
}
