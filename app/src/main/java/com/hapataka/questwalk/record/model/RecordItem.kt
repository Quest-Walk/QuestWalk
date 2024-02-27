package com.hapataka.questwalk.record.model

sealed class RecordItem {
    data class Header(
        val title: String
    ) : RecordItem()

    data class Result(
        val thumbnail: String,
        val isSuccess: Boolean
    ) : RecordItem()

    data class Achievement(
        val icon: String,
        val title: String,
    ) : RecordItem()
}
