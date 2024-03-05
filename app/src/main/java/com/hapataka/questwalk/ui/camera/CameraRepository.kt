package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CameraRepository @Inject constructor(@ApplicationContext private val context: Context) {

    fun saveBitmap(bitmap: Bitmap, filename: String): File {
        val file = File(context.filesDir, filename)
        val fos = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()

        return file
    }
    fun deleteBitmap(){
        // TODO : 이미지 처리 후 내부 저장소 에 이미지 삭제
    }


}