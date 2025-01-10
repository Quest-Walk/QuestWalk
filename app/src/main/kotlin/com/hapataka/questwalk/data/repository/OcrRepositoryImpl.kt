package com.hapataka.questwalk.data.repository

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.hapataka.questwalk.domain.repository.OcrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor() : OcrRepository {
    override suspend fun getWordFromImage(image: Bitmap): List<String> =
        withContext(Dispatchers.IO) {
            val recognizer =
                TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            val image = InputImage.fromBitmap(image, 0)
            val result = recognizer.process(image).await()
            val resultList = mutableListOf<String>()

            for (block in result.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        resultList.add(element.text)
                    }
                }
            }
            return@withContext resultList
        }
}