package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.entity.TmEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.repository.LocationRepository


class GetTmLocationUseCase(
    private val locationRepo: LocationRepository,
    private val dustRepo: DustRepository
) {
    suspend operator fun invoke(): TmEntity {
        val fullAddress = locationRepo.getAddress()?.getAddressLine(0) ?: return TmEntity("0","0")
        val address = filterEupMyeonDong(fullAddress)
        val map = mapOf(
            "serviceKey" to "vaXH1GPi1Tx19XQNGP2u25wMm5G/r4iAA7OZKcbQz7cVWKx+vwA+InIc3GcfBNVkF6QdQxiAtDV8+kt+TlFZAg==",
            "returnType" to "json",
            "umdName" to address
        )
        return dustRepo.getTmLocation(map)
    }

    private fun filterEupMyeonDong(address: String): String {
        address.split(" ").forEach {
            if (it.endsWith("읍") || it.endsWith("면") || it.endsWith("동")) {
                return it.trim()
            }
        }
        return "역삼동"
    }
}

