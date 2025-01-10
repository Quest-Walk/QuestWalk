package com.hapataka.questwalk.domain.repository

import android.net.Uri

interface ImageRepository {
    suspend fun setImage(uri: Uri, uid: String): Uri
}
