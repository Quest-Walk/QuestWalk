package com.hapataka.questwalk.ui.fragment.quest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestData(
    val keyWord: String = "",
    val level: Int = 0,
    var successItems: List<SuccessItem> = listOf(),
    val allUser: Long = 0,
    val isSuccess: Boolean = false
) : Parcelable {
    @Parcelize
    data class SuccessItem(
        val userId: String = "",
        val imageUrl: String = "",
        val registerAt: String = ""
    ): Parcelable
}