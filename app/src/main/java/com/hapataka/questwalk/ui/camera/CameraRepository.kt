package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
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
    fun contractBitmapWithCLAHE(
        bitmap: Bitmap?,
        clipLimit: Double,
        titlesGridSize: Double,
    ): Bitmap? {
        if (bitmap == null) return null
        val contractBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val originMat = Mat()
        Utils.bitmapToMat(contractBitmap,originMat)
        val mat = Mat()
        Utils.bitmapToMat(contractBitmap, mat)
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
        Imgproc.createCLAHE(clipLimit, Size(titlesGridSize, titlesGridSize)).apply(mat, mat)



        Imgproc.GaussianBlur(mat, mat, Size(31.0, 31.0), 0.0)
        //엣지 강화
//        Imgproc.Laplacian(mat,mat,mat.depth(),31)
//        Core.convertScaleAbs(mat,mat)
        val sobelX = Mat()
        val sobelY = Mat()
        Imgproc.Sobel(mat, sobelX, mat.depth(), 1, 0)
        Imgproc.Sobel(mat, sobelY, mat.depth(), 0, 1)
        Core.addWeighted(sobelX, 0.5, sobelY, 0.5, 2.0, mat)

        //이진화
        Imgproc.threshold(mat, mat, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
//        Imgproc.adaptiveThreshold(mat, mat, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//            Imgproc.THRESH_BINARY, 11, 4.0)

        //모폴리지 연산
        val kernel= Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,Size(7.0,7.0))

        Imgproc.morphologyEx(mat,mat, Imgproc.MORPH_OPEN, kernel)

        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_GRAY2RGBA)
        Core.bitwise_and(originMat,mat,mat)
        Utils.matToBitmap(mat, contractBitmap)

        return contractBitmap

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