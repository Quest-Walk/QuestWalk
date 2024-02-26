package com.hapataka.questwalk.domain.repository

import android.net.Uri

interface ImageRepository {
    fun uploadImage(uri: Uri)
    fun loadImage()
}