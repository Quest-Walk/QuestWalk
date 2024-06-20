package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.dto.DustDTO

interface DustRemoteDataSource {
    suspend fun getDustInfo(currentLocation: Pair<Float, Float>): DustDTO?
}