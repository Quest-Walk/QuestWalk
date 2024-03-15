package com.hapataka.questwalk.data.remote.repository

import com.hapataka.questwalk.data.remote.dto.dust.Dust
import com.hapataka.questwalk.data.remote.retrofit.RetrofitClient
import com.hapataka.questwalk.domain.repository.DustRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DustRepositoryImpl: DustRepository {
    private val dustService = RetrofitClient.dustApi

    override suspend fun getDust(queryMap: Map<String, String>): Dust = withContext(Dispatchers.IO) {
        dustService.getDust(queryMap)
    }

}