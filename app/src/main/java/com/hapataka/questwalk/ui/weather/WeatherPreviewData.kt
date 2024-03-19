package com.hapataka.questwalk.ui.weather

import com.google.android.datatransport.cct.StringMerger

data class WeatherPreviewData(
    val currentTmp: String,
    val sky: String,
    val precipType: String,
    val miseState: String,
    val choMiseState: String
)
