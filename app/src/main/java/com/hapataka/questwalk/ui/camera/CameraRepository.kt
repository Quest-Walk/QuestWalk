package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import org.opencv.android.Utils
import org.opencv.core.Mat
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
    fun contractBitmapWithCLAHE(bitmap: Bitmap?, clipLimit: Double, titlesGridSize: Double): Bitmap? {
        if (bitmap == null) return null
        val contractBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)
        val mat = Mat()
        Utils.bitmapToMat(contractBitmap,mat)
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY)
        Imgproc.createCLAHE(clipLimit,Size(titlesGridSize,titlesGridSize)).apply(mat,mat)
//        Imgproc.threshold(mat,mat,127.0,255.0,Imgproc.THRESH_BINARY)
//        Imgproc.GaussianBlur(mat,mat,Size(3.0,3.0),0.0)
        Utils.matToBitmap(mat,contractBitmap)

        return contractBitmap

    }


    fun deleteBitmap() {
        // TODO : 이미지 처리 후 내부 저장소 에 이미지 삭제
        file?.delete()
    }

}