package com.hapataka.questwalk.ui.activity.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
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
        file = File(context.filesDir, "resultImage.jpg")
        val fos = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
    }

    fun getImageUri(): Uri {
        return Uri.parse("file://${file?.path}")
    }

    fun preProcessBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        val contractBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val mat = Mat()
        Utils.bitmapToMat(contractBitmap, mat)
        val resultMat = processBitmapWithMask(mat)
        Utils.matToBitmap(resultMat, contractBitmap)

        return contractBitmap

    }

    private fun processBitmapWithMask(src: Mat): Mat {
        if (src.empty()) return Mat()
        val processedImage = Mat()
        val binary = Mat()
        val mask = Mat()
        val result = Mat()
        val resultBitmap = mutableListOf<Bitmap>()
        // 0
        var bitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(src, bitmap)
        resultBitmap.add(bitmap)

        val mat = src.clone()
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

        resultBitmap.add(dummyBitmap(mat))
        Imgproc.createCLAHE(200.0, getSize(10.0)).apply(mat, mat)

        resultBitmap.add(dummyBitmap(mat))

        Imgproc.GaussianBlur(mat, mat, getSize(51.0), 0.0)

        resultBitmap.add(dummyBitmap(mat))

        Imgproc.Laplacian(mat, mat, -1, 5, 3.0, 0.0, Core.BORDER_REPLICATE)

        resultBitmap.add(dummyBitmap(mat))

        Imgproc.threshold(mat, binary, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

        resultBitmap.add(dummyBitmap(binary))

        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, getKernel(Imgproc.MORPH_OPEN, 3.0))
        resultBitmap.add(dummyBitmap(binary))



        resultBitmap.add(dummyBitmap(binary))

        Core.bitwise_and(src, src, processedImage, binary)


        Core.bitwise_not(binary, mask)
        Core.bitwise_and(src, src, result, mask) 
        Core.addWeighted(processedImage, 1.5, result, 1.0, 1.0, result)

        //7
        resultBitmap.add(dummyBitmap(result))

        Imgproc.cvtColor(result, result, Imgproc.COLOR_RGB2GRAY)
        Imgproc.createCLAHE(150.0, Size(10.0, 10.0)).apply(result, result)

        return result
    }

    private fun getKernel(morphType: Int, size: Double) =
        Imgproc.getStructuringElement(morphType, getSize(size))

    private fun getSize(size : Double) = Size(size,size)

    private fun dummyBitmap(src: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(src, bitmap)
        return bitmap
    }
}

