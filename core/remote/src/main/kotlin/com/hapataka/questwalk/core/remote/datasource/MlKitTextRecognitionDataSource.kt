package com.hapataka.questwalk.core.remote.datasource

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.hapataka.questwalk.core.dataapi.datasource.TextRecognitionDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume

class MlKitTextRecognitionDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : TextRecognitionDataSource {

    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    override suspend fun recognizeText(filePath: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    continuation.resume(Result.failure(IllegalArgumentException("File not found: $filePath")))
                    return@suspendCancellableCoroutine
                }

                val inputImage = InputImage.fromFilePath(context, Uri.fromFile(file))

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        continuation.resume(Result.success(visionText.text))
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(Result.failure(exception))
                    }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
}
