package com.hapataka.questwalk.ui.quest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestStatsEntity (
    val keyWord: String,
    val level: Int,
    var successItems: Map<String, Int>
):Parcelable