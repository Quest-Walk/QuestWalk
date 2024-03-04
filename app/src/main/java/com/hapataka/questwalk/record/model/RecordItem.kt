package com.hapataka.questwalk.record.model

sealed class RecordItem {
    data class Header(
        val title: String
    ) : RecordItem()

    data class Result(
        val thumbnail: Int,
        val isSuccess: Boolean
    ) : RecordItem()

    data class Achievement(
        val icon: Int,
        val name: String,
        val description: String
    ) : RecordItem()
}
