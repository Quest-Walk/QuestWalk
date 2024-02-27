package com.hapataka.questwalk.data.firebase.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.hapataka.questwalk.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class ImageRepositoryImpl: ImageRepository {
    private val storage by lazy { Firebase.storage }
    private val storageRef by lazy { storage.reference }
    override suspend fun setImage(uri: Uri, uid: String): Uri = withContext(Dispatchers.IO) {
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val pathString = "${uid}${fileName}.png"
        val imageRef = storageRef.child(pathString)
        val uploadTask = imageRef.putFile(uri)

        uploadTask.await()
        return@withContext imageRef.downloadUrl.await()
    }
}
