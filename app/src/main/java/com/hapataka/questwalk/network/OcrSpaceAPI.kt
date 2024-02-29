package com.hapataka.questwalk.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OcrSpaceAPI {

    companion object{
        private const val API_REGION = "kor"
        private const val OCR_SPACE_API_KEY = "K89093548288957"
    }
//    @POST("image")
//    suspend fun getImageOcrResponse(
//        @Query("part") part: String = "snippet",
//    ) : VideoModel
}