package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
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

    /**
     * clipLimit : 각 타일 Histogram 할때, 임계값 제한
     * titlesGridSize : 이미지를 얼마나 많은 tile로 나눌것인지 결정
     */
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

        // 이미지 처리 과정
        val mat = src.clone()
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

        Imgproc.createCLAHE(100.0, Size(10.0, 10.0)).apply(mat, mat)


        Imgproc.GaussianBlur(mat, mat, Size(31.0, 31.0), 0.0)

        val sobelX = Mat()
        val sobelY = Mat()
        Imgproc.Sobel(mat, sobelX, mat.depth(), 1, 0)
        Imgproc.Sobel(mat, sobelY, mat.depth(), 0, 1)
        Core.addWeighted(sobelX, 0.5, sobelY, 0.5, 2.0, mat)

        Imgproc.threshold(mat, binary, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

        // 흰색 부분만 처리하기 위한 마스크 생성
        Core.bitwise_and(src, src, processedImage, binary)

        // 검은색 부분은 원본 이미지 사용, 흰색 부분은 처리된 이미지 사용
        Core.bitwise_not(binary, mask) // 흰색 부분을 제외한 나머지를 마스크로 생성
        Core.bitwise_and(src, src, result, mask) // 원본에서 검은색 부분만 추출
        Core.addWeighted(processedImage,3.0,result,1.0,0.0,result)

        return result
    }

    private fun findMinMaxBrightness(inputImage: Mat): Pair<Double, Double> {

        val result = Core.minMaxLoc(inputImage)
        return Pair(result.minVal, result.maxVal)
    }


    fun deleteBitmap() {
        // TODO : 이미지 처리 후 내부 저장소 에 이미지 삭제
        file?.delete()
    }

}