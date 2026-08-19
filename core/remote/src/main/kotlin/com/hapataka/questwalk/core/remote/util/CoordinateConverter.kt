package com.hapataka.questwalk.core.remote.util

import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

object CoordinateConverter {

    /**
     * 위경도를 기상청 격자 좌표로 변환
     */
    fun toGridXY(latitude: Double, longitude: Double): Pair<Int, Int> {
        val RE = 6371.00877
        val GRID = 5.0
        val SLAT1 = 30.0
        val SLAT2 = 60.0
        val OLON = 126.0
        val OLAT = 38.0
        val XO = 43.0
        val YO = 136.0

        val DEGRAD = PI / 180.0

        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        val sn = tan(PI * 0.25 + slat2 * 0.5) / tan(PI * 0.25 + slat1 * 0.5)
            .let { ln(it) / ln(tan(PI * 0.25 + slat2 * 0.5) / tan(PI * 0.25 + slat1 * 0.5)) }
        val snCalc = ln(cos(slat1) / cos(slat2)) / ln(tan(PI * 0.25 + slat2 * 0.5) / tan(PI * 0.25 + slat1 * 0.5))
        val sf = tan(PI * 0.25 + slat1 * 0.5).pow(snCalc) * cos(slat1) / snCalc
        val ro = re * sf / tan(PI * 0.25 + olat * 0.5).pow(snCalc)

        val ra = re * sf / tan(PI * 0.25 + latitude * DEGRAD * 0.5).pow(snCalc)
        var theta = longitude * DEGRAD - olon

        if (theta > PI) theta -= 2.0 * PI
        if (theta < -PI) theta += 2.0 * PI

        theta *= snCalc

        val x = (ra * sin(theta) + XO + 0.5).toInt()
        val y = (ro - ra * cos(theta) + YO + 0.5).toInt()

        return Pair(x, y)
    }

    private fun ln(x: Double): Double = kotlin.math.ln(x)

    /**
     * 위경도를 TM 좌표(Bessel)로 변환 (미세먼지 측정소 조회용)
     */
    fun toBesselTM(latitude: Double, longitude: Double): Pair<Double, Double> {
        val wgs84Proj = "+proj=longlat +ellps=bessel +no_defs"
        val wgs84System = CRSFactory().createFromParameters("WGS84", wgs84Proj)

        val besselProj = "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"
        val besselSystem = CRSFactory().createFromParameters("Bessel", besselProj)

        val sourceCoord = ProjCoordinate(longitude, latitude)
        val targetCoord = ProjCoordinate()

        val transform = CoordinateTransformFactory().createTransform(wgs84System, besselSystem)
        transform.transform(sourceCoord, targetCoord)

        return Pair(targetCoord.x, targetCoord.y)
    }
}
