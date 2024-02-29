package com.hapataka.questwalk.network

import com.hapataka.questwalk.model.reponseocr.ResponseOcr
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrSpaceAPI {

    companion object {
        private const val OCR_SPACE_API_KEY = "K89093548288957"
        private const val FILE_TYPE = "JPG"
    }

    @Multipart
    @POST("image")
    suspend fun getImageOcrResponse(
        @Part("file") image: MultipartBody.Part,
        @Part("apikey") apikey: RequestBody = OCR_SPACE_API_KEY.toRequestBody(),
        @Part("language") language: RequestBody = "kor".toRequestBody(),
        @Part("isOverlayRequired") isOverlayRequired: RequestBody = "true".toRequestBody(),
        @Part("filetype") fileType: RequestBody = FILE_TYPE.toRequestBody(),
        @Part("detectOrientation") orientation: RequestBody = "true".toRequestBody(),
        @Part("scale") scale: RequestBody = "true".toRequestBody(),
        @Part("OCREngine") engine: RequestBody = "3".toRequestBody(),
    ): Call<ResponseOcr>


}