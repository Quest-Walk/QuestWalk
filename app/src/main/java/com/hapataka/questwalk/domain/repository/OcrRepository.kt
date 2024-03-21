package com.hapataka.questwalk.domain.repository

import android.graphics.Bitmap

interface OcrRepository {
    suspend fun getWordFromImage(image: Bitmap): List<String>
}