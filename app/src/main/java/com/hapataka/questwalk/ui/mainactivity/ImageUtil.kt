package com.hapataka.questwalk.ui.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ImageUtil(val context: Context) {
    private var file: File? = null
    fun setCaptureImage(image: ImageProxy): Bitmap {
        val bitmap = image.toBitmap()
        val matrix = Matrix()

        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        val postBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        GlobalScope.launch {
            saveImage(postBitmap)
            this.cancel()
        }
        return postBitmap
    }

    private fun saveImage(bitmap: Bitmap) {
        file = File(context.filesDir, "resultImage.png")
        val fos = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    }

    fun getImageUri(): Uri {
        return Uri.parse("file://${file?.path}")
    }

}

