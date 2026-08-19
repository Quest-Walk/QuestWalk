package com.hapataka.questwalk.core.dataapi.datasource

interface TextRecognitionDataSource {
    suspend fun recognizeText(filePath: String): Result<String>
}
