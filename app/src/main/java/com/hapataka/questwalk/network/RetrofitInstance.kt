package com.hapataka.questwalk.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val OCR_ENDPOINT = "https://api.ocr.space/parse/"


    val ocrSpaceApi: OcrSpaceAPI by lazy { buildRetrofit(OCR_ENDPOINT).create(OcrSpaceAPI::class.java) }

    private fun buildRetrofit(baseUri: String): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUri)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                    .readTimeout(2,TimeUnit.MINUTES)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }
}