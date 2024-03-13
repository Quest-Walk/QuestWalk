package com.hapataka.questwalk.ui.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import com.hapataka.questwalk.ui.record.TAG
import java.io.File
import java.io.FileOutputStream
class ImageUtil(val context: Context) {
    private var file: File? = null
    fun setCaptureImage(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())

        buffer.get(bytes)

        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()

        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        val postBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        saveImage(postBitmap, "resultImage.png")

        return postBitmap
    }

    private fun saveImage(bitmap: Bitmap, filename: String) {
        file = File(context.filesDir, filename)
        val fos = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    }

    fun getImageUri(): Uri {
        Log.i(TAG, "path: ${file?.path}")
        return Uri.parse("file://${file?.path}")
    }

}

