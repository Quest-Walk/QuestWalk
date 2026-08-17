package com.hapataka.questwalk.core.remote.datasource

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.hapataka.questwalk.core.dataapi.datasource.ImageStorageDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseStorageDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : ImageStorageDataSource {

    private val storage = FirebaseStorage.getInstance()

    override suspend fun uploadImage(filePath: String, remotePath: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    continuation.resume(Result.failure(IllegalArgumentException("File not found: $filePath")))
                    return@suspendCancellableCoroutine
                }

                val storageRef = storage.reference.child(remotePath)
                val uploadTask = storageRef.putFile(Uri.fromFile(file))

                uploadTask
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        storageRef.downloadUrl
                    }
                    .addOnSuccessListener { downloadUrl ->
                        continuation.resume(Result.success(downloadUrl.toString()))
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(Result.failure(exception))
                    }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }

    override suspend fun copyToLocalStorage(filePath: String, targetFileName: String): Result<String> {
        return runCatching {
            val sourceFile = File(filePath)
            if (!sourceFile.exists()) {
                throw IllegalArgumentException("Source file not found: $filePath")
            }

            val questImagesDir = File(context.filesDir, "quest_images")
            if (!questImagesDir.exists()) {
                questImagesDir.mkdirs()
            }

            val targetFile = File(questImagesDir, targetFileName)
            sourceFile.copyTo(targetFile, overwrite = true)

            targetFile.absolutePath
        }
    }

    override suspend fun deleteFile(filePath: String): Result<Unit> {
        return runCatching {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
