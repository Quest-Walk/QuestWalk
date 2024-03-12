package com.hapataka.questwalk.ui.quest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class QuestData(
    val keyWord: String = "",
    val level: Int = 0,
    var successItems: List<SuccessItem> = listOf()
) : Parcelable {
    @Parcelize
    data class SuccessItem(
        val userId: String = "",
        val imageUrl: String = "",
        val registerAt: String = ""
    ): Parcelable
}