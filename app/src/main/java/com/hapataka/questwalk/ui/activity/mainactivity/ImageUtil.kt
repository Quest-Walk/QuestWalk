package com.hapataka.questwalk.ui.activity.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.ImageProxy
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject

class ImageUtil @Inject constructor(@ApplicationContext val context: Context) {
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
        // 이미지 처리 과정
        val mat = src.clone()
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
        // 1
        resultBitmap.add(dummyBitmap(mat))
        Imgproc.createCLAHE(200.0, getSize(10.0)).apply(mat, mat)
        // 2
        resultBitmap.add(dummyBitmap(mat))

        Imgproc.GaussianBlur(mat, mat, getSize(51.0), 0.0)
        // 3
        resultBitmap.add(dummyBitmap(mat))


        Imgproc.Laplacian(mat, mat, -1, 5, 3.0, 0.0, Core.BORDER_REPLICATE)

        // 4
        resultBitmap.add(dummyBitmap(mat))
//
        Imgproc.threshold(mat, binary, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
        // 5
        resultBitmap.add(dummyBitmap(binary))

        //모폴리지 연산

        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, getKernel(Imgproc.MORPH_OPEN, 3.0))
        resultBitmap.add(dummyBitmap(binary))


        // 6
        resultBitmap.add(dummyBitmap(binary))
        // 흰색 부분만 처리하기 위한 마스크 생성
        Core.bitwise_and(src, src, processedImage, binary)

        // 검은색 부분은 원본 이미지 사용, 흰색 부분은 처리된 이미지 사용
        Core.bitwise_not(binary, mask) // 흰색 부분을 제외한 나머지를 마스크로 생성
        Core.bitwise_and(src, src, result, mask) // 원본에서 검은색 부분만 추출
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

