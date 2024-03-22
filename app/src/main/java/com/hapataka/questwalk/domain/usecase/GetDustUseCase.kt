package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.TmEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.repository.LocationRepository

class GetDustUseCase(
    private val locationRepo: LocationRepository,
    private val dustRepo: DustRepository
) {
    suspend operator fun invoke(): DustEntity {
        val fullAddress = locationRepo.getAddress()?.getAddressLine(0) ?: return DustEntity(0,0)
        val address = filterEupMyeonDong(fullAddress)
        val tmQueryMap = requestTmQueryMap(address)
        val tmLocation = dustRepo.getTmLocation(tmQueryMap)

        val stationQueryMap = requestStationQueryMap(tmLocation)
        val stationName = dustRepo.getStation(stationQueryMap).stationName

        val dustQueryMap = requestDustQueryMap(stationName)
        return dustRepo.getDustInfo(dustQueryMap)
    }

    private fun filterEupMyeonDong(address: String): String {
        address.split(" ").forEach {
            if (it.endsWith("읍") || it.endsWith("면") || it.endsWith("동")) {
                return it.trim()
            }
        }
        return "역삼동"
    }

    private fun requestTmQueryMap(address: String): Map<String, String> {
        return mapOf(
            "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
            "returnType" to "json",
            "umdName" to address
        )
    }

    private fun requestStationQueryMap(tmLocation: TmEntity): Map<String, String> {
        return mapOf(
            "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
            "returnType" to "json",
            "tmX" to tmLocation.tmx,
            "tmY" to tmLocation.tmy
        )
    }

    private fun requestDustQueryMap(station: String): Map<String, String> {
        return mapOf(
            "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
            "returnType" to "json",
            "stationName" to station,
            "dataTerm" to "DAILY",
            "ver" to "1.0"
        )
    }
}