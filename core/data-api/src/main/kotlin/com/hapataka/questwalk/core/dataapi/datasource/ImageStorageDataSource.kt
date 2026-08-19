package com.hapataka.questwalk.core.dataapi.datasource

interface ImageStorageDataSource {
    suspend fun uploadImage(filePath: String, remotePath: String): Result<String>
    suspend fun copyToLocalStorage(filePath: String, targetFileName: String): Result<String>
    suspend fun deleteFile(filePath: String): Result<Unit>
}
