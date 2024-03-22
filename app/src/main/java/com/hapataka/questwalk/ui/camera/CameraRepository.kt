package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CameraRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private var file: File? = null
    fun saveBitmap(bitmap: Bitmap, filename: String): File {
        file = File(context.filesDir, filename)
        val fos = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

        return file as File
    }

    fun resizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()

        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun toGrayScaleBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null

        val width: Int = bitmap.width
        val height: Int = bitmap.height

        val grayscaleBitmap = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()

        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)

        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return grayscaleBitmap
    }

    /**
     * contract 1.0 변화 없음
     * contract > 1.0  대비 증가 <1.0 대비 감소
     */
    fun contractBitmap(bitmap: Bitmap?, contract: Float): Bitmap? {
        if (bitmap == null) return null
        val contractBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val cm = ColorMatrix(
            floatArrayOf(
                contract, 0f, 0f, 0f, 0f, //R
                0f, contract, 0f, 0f, 0f, //G
                0f, 0f, contract, 0f, 0f, //B
                0f, 0f, 0f, 1f, 0f // A
            )
        )
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        val canvas = Canvas(contractBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return contractBitmap
    }

    fun deleteBitmap() {
        // TODO : 이미지 처리 후 내부 저장소 에 이미지 삭제
        file?.delete()
    }
}