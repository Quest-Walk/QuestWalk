package com.hapataka.questwalk.data.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.hapataka.questwalk.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor() : ImageRepository {
    private val storage by lazy { Firebase.storage }
    private val storageRef by lazy { storage.reference }
    override suspend fun setImage(uri: Uri, uid: String): Uri = withContext(Dispatchers.IO) {
        val fileName = (uid + System.currentTimeMillis()).toHash()
        val pathString = "${fileName}.png"
        val imageRef = storageRef.child(pathString)
        val uploadTask = imageRef.putFile(uri)

        uploadTask.await()
        return@withContext imageRef.downloadUrl.await()
    }

    private fun String.toHash(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(this.toByteArray())

        return hash.joinToString("") { "%02x".format(it) }
    }
}
